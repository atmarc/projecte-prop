package dominio.clases;

import java.util.ArrayList;

/**
 *  @brief     	Extension de la clase Compressor mediante el algoritmo LZ-W.
 *  @details   	La clase que implementa la compression de un fichero mediante el
 *  			algoritmo LZW
 *  @author    	Andrei Mihalache
 */
public class Compressor_LZW extends Compressor {
	private static final String extension = ".lzw"; ///< extension del los ficheros comprimidos por el algoritmo
	private static final int BYTE_SIZE = 8;	///< dimension de un byte
	private Tree dictionary;	///< el diccionario de los patrones encontrados
	private int nextIndex = 0;	///< el indice de la siguiente palabra a insertar
	private ArrayList<Byte> pattern; ///< el contenedor para guarda el patron
	private int codewordSize;   ///< la longitud en bits para escribir la codificación

	/**
	 * @brief Crea un objecto compressor con el diccionario básico.
	 */
	public Compressor_LZW() {
		inicializar();
	}

	/**
	 * @brief Retorna la extension de los ficheros comprimidos con este algoritmo
	 * @return la extension de los ficheros comprimidos
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @brief 	Comprime un fichero mediante el algoritmo LZW
	 * @details La funcion lee byte a byte desde el fichero a comprimir mediante
	 * 			el controlador, y construye la palabra mas grande de bytes seguides que no esta
	 * 			en el diccionario hasta el momento. Cuando la encuentra, la añade al diccionario
	 * 			con el menor indice no utilizado, escribe el código de esta palabra menos el último
	 * 			carácter en la salida y empieza a buscar otra empezando con el ultimo carácter leído.
	 * @pre		El input stream reader y el output stream reader estan creados e inicializados
	 * 			con los los ficheros de entrada y salida respectivamente
	 * @post	El fichero de salida contiene el contenido comprimido del fichero de la entrada
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
			} else pattern.add(B);
		}
		if (pattern.size() > 0) {
			codeword = dictionary.find(toByteArray(pattern));
			byte[] codewordAsByteArray = toByteArray(codeword);
			controller.writeBytes(codewordAsByteArray);
		}
	}

	/**
	 * @brief Convierte un ArrayList de Bytes en un byte array
	 * @param p el ArrayList a convertir
	 * @return el byte array equivalente a {@code p}
	 */
	private byte[] toByteArray(ArrayList<Byte> p) {
		byte[] res = new byte[p.size()];
		for (int i = 0; i < p.size(); ++i) res[i] = p.get(i);
		return res;
	}

	/**
	 * @brief Concatena un byte array con un byte
	 * @param p el byte array a concatenar
	 * @param b el byte a concatenar
	 * @return un byte array que representa la
	 * concatenacion de {@code p} y {@code b}
	 */
	private byte[] concatenate(ArrayList<Byte> p, byte b) {
		byte[] res = new byte[p.size() + 1];
		for (int i = 0; i < p.size(); ++i) res[i] = p.get(i);
		res[p.size()] = b;
		return res;
	}

	/**
	 * @brief Convierte un int en un array con elementos de 8 bits
	 * (Es igual a pasar un integer desde la base 10 a base 256)
	 * @param codeword número a convertir
	 * @return un array con la representación de {@code codeword} en base 256
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
	 * @brief Inicializa el diccionario del compresor
	 * @details Inserta todos los caracteres de ASCII extendido en el
	 * diccionario del compressor. Esto forma el diccionario basico.
	 * Ademas, establece la longitud de los codigos a escribir a 16 bits
	 * y reinicia la palabra acumulada
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