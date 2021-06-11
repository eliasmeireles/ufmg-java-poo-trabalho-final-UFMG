package br.com.ufmg.coltec.locamais.filemanager;

import br.com.ufmg.coltec.locamais.model.Cliente;

import java.io.IOException;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.*;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.*;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getAsJson;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getClientesFromJson;

/**
 * Created by elias on 18/11/18.
 */
public class ClienteFileManager {


    /**
     * @Metodo que recebe um lista de clientes e a salva em um arquivo.
     * */
    public static void saveClientes(List<Cliente> clientes) throws IOException {
        createFolder(CLIENTES_DIRECTORY);
        createFolder(CLIENTES_IMAGE_DIRECTORY);
        writeFile(CLIENTES_JSON, getAsJson(clientes));
    }


    /**
     * @Metodo que localiza todos os clientes cadastrados no
     * sistema.
     * */
    public static List<Cliente> getClientes() {
        createFolder(CLIENTES_DIRECTORY);
        createFolder(CLIENTES_IMAGE_DIRECTORY);
        String fileEntry = getFileEntry(CLIENTES_JSON);
        return getClientesFromJson(fileEntry);
    }
}
