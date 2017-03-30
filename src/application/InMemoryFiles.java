package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryFiles {
	private final Path filePath;
	private List<Path> filePaths;
	private int fileIndex;
	
	public InMemoryFiles(String filePathStr) {
		this.filePath = Paths.get(filePathStr);	
		this.filePaths = new ArrayList<>();
		this.fileIndex = 0;
		try {
			this.filePaths = Files.list(filePath).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public Path getActualFile() {
		return filePaths.get(fileIndex);
	}
	
	public Path removeActualFile() {
		Path path =  filePaths.remove(fileIndex);
		renewIndex();
		return path;
	}
	
	
	public boolean removeFile(Path file) {
		return filePaths.remove(file);
	}
	
	private void renewIndex() {
		if (fileIndex >= filePaths.size()) fileIndex = filePaths.size() - 1;
		if (fileIndex < 0) fileIndex = 0;
	}
	
	public Path getNext() {
		return filePaths.get(getNextFileIndex());
	}
	
	public Path getPrev() {
		return filePaths.get(getPrevFileIndex());
	}
	
	private int getNextFileIndex() {
		if (++fileIndex >= filePaths.size()) fileIndex = 0;
		return fileIndex;
	}
	
	private int getPrevFileIndex() {
		if (--fileIndex < 0) fileIndex = filePaths.size() - 1;
		return fileIndex;
	}
	
	public Path getFilePath() {
		return filePath;
	}

	public List<Path> getFilePaths() {
		return filePaths;
	}

	public int getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(int fileIndex) {
		this.fileIndex = fileIndex;
	}

	
	
}
