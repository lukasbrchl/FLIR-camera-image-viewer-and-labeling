import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;

public class Utils {
	
	public static <T> void updateFXControl(final ObjectProperty<T> property, final T value)	{
		Platform.runLater(() -> {
			property.set(value);
		});
	}
	
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
}
