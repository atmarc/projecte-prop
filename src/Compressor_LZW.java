import java.io.*;
import java.util.ArrayList;

/*!
 *  \brief     Extension de la clase Compressor mediante el algoritmo LZ-W.
 *  \details
 *  \author    Andrei Mihalache
 */
public class Compressor_LZW extends Compressor {
	private static final String extension = ".lzw";
	private static final int BYTE_SIZE = 8;
	private Tree dictionary;
	private int nextIndex = 0;
	private ArrayList<Byte> pattern;
	private int codewordSize;   // la longitud en bits para escribir la codificación

	/**
	 * Crea un objecto compressor con el diccionario básico.
	 */
	public Compressor_LZW() {
		inicializar();
	}

	/**
	 * Retorna la extension de los ficheros comprimidos con esta clase
	 * @return la extension de los ficheros comprimidos
	 */
	protected String getExtension() {
		return ".lzw";
	}


	/**
	 * Comprime un fichero mediante el algoritmo LZW
	 */
	public void compress() {
		inicializar();
		int nx;
		int codeword;
		while ((nx = controller.readByte()) != -1) {
			byte B = (byte) nx;
			byte[] patternByteArray = toByteArray(pattern);
			byte[] patternPlusNextChar = concatenate(pattern, B);
			if (dictionary.find(patternPlusNextChar) == -1) {
				codeword = dictionary.find(patternByteArray);
				byte[] codewordAsByteArray = toByteArray(codeword);
				controller.writeBytes(codewordAsByteArray);
				dictionary.insert(patternPlusNextChar, nextIndex);
				++nextIndex;
				if (nextIndex >= (1 << codewordSize)) {
					codewordSize += BYTE_SIZE;
				}
				pattern = new ArrayList<>();
				pattern.add(B);
			}
			else pattern.add(B);
		}
		if (pattern.size() > 0) {
		codeword = dictionary.find(toByteArray(pattern));
		byte[] codewordAsByteArray = toByteArray(codeword);
			controller.writeBytes(codewordAsByteArray);
		}
	}

	/**
	 * Convierte un ArrayList de Bytes en un byte array
	 * @param p el ArrayList a convertir
	 * @return	el byte array equivalente a {@code p}
	 */
	private byte[] toByteArray(ArrayList<Byte> p) {
		byte[] res = new byte[p.size()];
		for (int i = 0; i < p.size(); ++i) res[i] = p.get(i);
		return res;
	}

	/**
	 * Concatena un byte array con un byte
	 * @param p el byte array a concatenar
	 * @param b el byte a concatenar
	 * @return un byte array que representa la
	 * concatenacion de {@code p} y {@code b}
	 */
	private byte[] concatenate(ArrayList<Byte> p, byte b) {
		byte[] res = new byte[p.size()+1];
		for (int i = 0; i < p.size(); ++i) res[i] = p.get(i);
		res[p.size()] = b;
		return res;
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
		nextIndex = 0;
		dictionary = new Tree(0);
		byte[] b = new byte[1];
		while (nextIndex < 256) {
			b[0] = (byte) nextIndex;
			dictionary.insert(b, nextIndex);
			++nextIndex;
		}
		codewordSize = 16;
		pattern = new ArrayList<>();
	}

}