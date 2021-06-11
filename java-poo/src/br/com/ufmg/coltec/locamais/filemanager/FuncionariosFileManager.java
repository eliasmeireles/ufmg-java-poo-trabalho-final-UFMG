package br.com.ufmg.coltec.locamais.filemanager;

import br.com.ufmg.coltec.locamais.model.Funcionario;

import java.io.IOException;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.*;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FUNCIONARIOS_DIRECTORY;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FUNCIONARIOS_JSON;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getAsJson;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getFuncionariosFromJson;

/**
 * Created by elias on 15/11/18.
 */
public class FuncionariosFileManager {

    /**
     * @Metodo que recebe uma lista de funcionários e salva essa lista no sistema.
     * */
    public static void saveFuncionarios(List<Funcionario> funcionarios) throws IOException {
        createFolder(FUNCIONARIOS_DIRECTORY);
        String json = getAsJson(funcionarios);
        writeFile(FUNCIONARIOS_JSON, json);
    }


    /**
     * @Metodo que retorna uma lista de funcionários já cadastrados no sistema.
     * */
    public static List<Funcionario> getFuncionariosCadastrados() {
        createFolder(FUNCIONARIOS_DIRECTORY);
        String fileEntry = getFileEntry(FUNCIONARIOS_JSON);
        return getFuncionariosFromJson(fileEntry);
    }

}
