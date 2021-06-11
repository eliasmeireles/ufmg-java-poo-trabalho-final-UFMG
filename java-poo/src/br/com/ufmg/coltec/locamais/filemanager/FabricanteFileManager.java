package br.com.ufmg.coltec.locamais.filemanager;

import br.com.ufmg.coltec.locamais.model.Fabricante;

import java.io.IOException;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.*;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FABRICANTES_DIRECTORY;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FABRICANTES_JSON;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getAsJson;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getFabricantesFromJson;

/**
 * Created by elias on 10/11/18.
 */
public class FabricanteFileManager {

    /**
     * @Metodo que recebe por parametro uma lista de fabricantes, localiza todos
     * os fabricantes já cadastrados no sistema e verifica se à algum fabricante duplicado,
     * caso aja, esse fabricante não será salvo.
     * */
    public static void saveFrabricantes(List<Fabricante> fabricantes) throws IOException {
        createFolder(FABRICANTES_DIRECTORY);

        String fileEntry = getFileEntry(FABRICANTES_JSON);
        List<Fabricante> fabricantesFromJson = getFabricantesFromJson(fileEntry);

        fabricantes.forEach(fabricante -> {
            if (!contains(fabricantesFromJson, fabricante.getNome())) fabricantesFromJson.add(fabricante);
        });

        String json = getAsJson(fabricantesFromJson);
        writeFile(FABRICANTES_JSON, json);
    }

    /**
     * @Metodo que analiza o fabricante com um determinado nome, caso o nome seja igual ao
     * nome do fabricante, seta retorna um valor verdadeiro.
     * */
    private static boolean contains(List<Fabricante> fabricantes, String nome) {
        boolean[] contains = {false};
        fabricantes.forEach(fabricante -> {
            if (fabricante.getNome().toLowerCase().equals(nome.toLowerCase())) {
                contains[0] = true;
                return;
            }
        });
        return contains[0];
    }

    /**
     * @Metodo que retorna uma lista de fabricantes já cadastrados no sistema.
     * */
    public static List<Fabricante> getFabricantesFromJsonFile() {
        String fileEntry = getFileEntry(FABRICANTES_JSON);
        return getFabricantesFromJson(fileEntry);
    }
}
