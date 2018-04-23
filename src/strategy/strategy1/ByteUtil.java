package strategy.strategy1;

public class ByteUtil {

	public static byte[] toByteArray(String binaryString) {
		byte[] solution = new byte[binaryString.length()];
		// Loop through each character of our string and save it in our byte
		// array
		for (int i = 0; i < binaryString.length(); i++) {
			String character = binaryString.substring(i, i + 1);
			if (character.contains("0") || character.contains("1")) {
				solution[i] = Byte.parseByte(character);
			} else {
				solution[i] = 0;
			}
		}
		return solution;
	}

	public static String arrayToString(byte[] byteArray) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < 3; j++) {
			sb.append(byteArray[j]);
		}
		return sb.toString();
	}

}
