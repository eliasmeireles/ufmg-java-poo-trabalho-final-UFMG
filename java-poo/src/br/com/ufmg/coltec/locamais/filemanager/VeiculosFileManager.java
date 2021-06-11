package br.com.ufmg.coltec.locamais.filemanager;

import br.com.ufmg.coltec.locamais.model.Veiculo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.*;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.*;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getAsJson;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getVeiculosFromJson;

/**
 * Created by elias on 10/11/18.
 */
public class VeiculosFileManager {

    /**
     * @Metodo que recebe um objeto do tipo veículo por paramentro,
     * localiza todos os veículos já cadastrados no sistema e insere
     * esse veículo nessa lista e salva essa lista novamente.
     * */
    public static void savarVeiculo(List<Veiculo> veiculos) throws IOException {

        createFolder(VEICULOS_DIRECTORY);
        createFolder(VEICULOS_IMAGE_DIRECTORY);
        String json = getAsJson(veiculos);
        writeFile(VEICULOS_JSON, json);
    }

    /**
     * @Metodo que retorna uma lista de veículos já cadastrados
     * no sistemas.
     * */
    public static List<Veiculo> getVeiculos() {
        createFolder(VEICULOS_DIRECTORY);
        createFolder(VEICULOS_IMAGE_DIRECTORY);

        String fileEntry = getFileEntry(VEICULOS_JSON);
        List<Veiculo> veiculosFromJson = getVeiculosFromJson(fileEntry);
        Collections.sort(veiculosFromJson);
        return veiculosFromJson;
    }
}
