import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LZWCompressor extends Compressor {
	private static final String extension = ".zero";
	private static final int BYTE_SIZE = 8;
	private HashMap<String, Integer> dictionary;
	private StringBuilder patternBuilder;
	private int codewordSize;   // la longitud en bits para escribir la codificación

	/**
	 * Crea un objecto compressor con el diccionario básico.
	 */
	public LZWCompressor() {
		this.inicializar();
	}


	/**
	 * @param file El fichero desde cual se tiene que calcular el nombre
	 * @return El nombre con la extension del fichero a comprimir
	 */
	private String getCompressedName(File file) {
		String fileName = file.getPath();
		int pos = fileName.lastIndexOf('.');
		String compressedFileName;
		if (pos != -1) compressedFileName = fileName.substring(0, pos);
		else throw new IllegalArgumentException("Nombre de fichero incorrecto");
		return compressedFileName + extension;
	}

	public void compress() {
		inicializar();
		int readByte;
		int codeword;
		int nr = 0;
		while ((readByte = super.readByte()) != -1) {
			char c = (char) readByte;
			String pattern = patternBuilder.toString();
			if (!dictionary.containsKey(pattern + c)) {
				codeword = dictionary.get(pattern);
				byte[] codewordAsByteArray = toByteArray(codeword);
				super.writeBytes(codewordAsByteArray);
				nr += codewordAsByteArray.length;
				int index = dictionary.size();
				dictionary.put(pattern + c, index);
				if (dictionary.size() >= (1 << codewordSize)) {
					codewordSize += BYTE_SIZE;
				}
				patternBuilder = new StringBuilder();
				patternBuilder.append(c);
			} else patternBuilder.append(c);
		}
		if (patternBuilder.length() > 0) {
			codeword = dictionary.get(patternBuilder.toString());
			byte[] codewordAsByteArray = toByteArray(codeword);
			super.writeBytes(codewordAsByteArray);
			nr += codewordAsByteArray.length;
		}
		System.out.println("It: " + nr);
	}

	/**
	 * @param data cadena de caracteres
	 * @return una lista de enteros que representa la cadena {@code data} en forma comprimida
	 */
	public ArrayList<Integer> compressString(String data) {
		ArrayList<Integer> outList = new ArrayList<>();
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			String s = patternBuilder.toString();
			if (!dictionary.containsKey(s + c)) {
				outList.add(dictionary.get(s));
				int temp = dictionary.size();
				dictionary.put(s + c, temp);
				patternBuilder = new StringBuilder();
				patternBuilder.append(c);
			} else patternBuilder.append(c);
		}
		if (patternBuilder.length() > 0) outList.add(dictionary.get(patternBuilder.toString()));
		return outList;
	}

	public void compress(String path) {
		compress(new File(path));
	}

	/**
	 * Comprime un fichero text que recibe como parametro y crea un nuevo fichero con el contenido comprimido
	 *
	 * @param file el fichero a comprimir
	 */
	public void compress(File file) {
		inicializar();
		File compressedFile = new File(getCompressedName(file));
		try (BufferedInputStream bufferedInputStream =
					 new BufferedInputStream(new FileInputStream(file.getPath()));
			 BufferedOutputStream bufferedOutputStream =
					 new BufferedOutputStream(new FileOutputStream(compressedFile.getPath()))
		) {
			int readByte;
			int codeword;
			int nr = 0;
			while ((readByte = bufferedInputStream.read()) != -1) {
				char c = (char) readByte;
				String pattern = patternBuilder.toString();
				if (!dictionary.containsKey(pattern + c)) {
					codeword = dictionary.get(pattern);
					byte[] codewordAsByteArray = toByteArray(codeword);
					bufferedOutputStream.write(codewordAsByteArray);
					nr += codewordAsByteArray.length;
					int index = dictionary.size();
					dictionary.put(pattern + c, index);
					if (dictionary.size() >= (1 << codewordSize)) {
						codewordSize += BYTE_SIZE;
					}
					patternBuilder = new StringBuilder();
					patternBuilder.append(c);
				} else patternBuilder.append(c);
			}
			if (patternBuilder.length() > 0) {
				codeword = dictionary.get(patternBuilder.toString());
				byte[] codewordAsByteArray = toByteArray(codeword);
				bufferedOutputStream.write(codewordAsByteArray);
				nr += codewordAsByteArray.length;
			}
			System.out.println("It: " + nr);
		} catch (FileNotFoundException e) {
			System.out.println("Fichero no encontrado\n" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error de lectura/escritura\n" + e.getMessage());
		}
	}

	/**
	 * Convierte un int en un array con elementos de 8 bits
	 * (Es igual a pasar un integer desde la base 10 a base 256)
	 *
	 * @param codeword número a convertir
	 * @return un array con la representación de {@code codeword}
	 */
	private byte[] toByteArray(int codeword) {
		byte[] codewordAsByte = new byte[codewordSize / BYTE_SIZE];
		for (int i = codewordAsByte.length - 1; i >= 0; --i) {
			codewordAsByte[i] = (byte) (codeword & 0xFF);
			codeword >>= 8;
		}
		return codewordAsByte;
	}

	/**
	 * Inicializa el diccionario del compresor al diccionario básico
	 */
	private void inicializar() {
		dictionary = new HashMap<>();
		for (char i = 0; i < 256; ++i) dictionary.put(String.valueOf(i), (int) i);
		codewordSize = 16;
		patternBuilder = new StringBuilder();
	}

}