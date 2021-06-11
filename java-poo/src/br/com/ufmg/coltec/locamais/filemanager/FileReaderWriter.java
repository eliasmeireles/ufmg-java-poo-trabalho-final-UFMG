package br.com.ufmg.coltec.locamais.filemanager;

import java.io.*;

/**
 * Created by elias on 10/11/18.
 */
public class FileReaderWriter {

    /**
     * @Metodo que recebe uma URL de um determiado diretório
     * e cria esse diretório caso ele ainda não exista.
     * */
    public static void createFolder(String folderPath) {
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    /**
     * @Metodo que recebe uma URL de um determiado arquivo
     * e cria esse arquivo caso ele ainda não exista.
     * */
    public static void writeFile(String filePath, String fileEntry) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(fileEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     *@Metodo que retorna o conteúdo de um determiado arquivo,
     * esse metodo recebe por parametro a URL de onde está esse
     * arquivo.
     * */
    public static String getFileEntry(String filePath) {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return  sb.toString();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
