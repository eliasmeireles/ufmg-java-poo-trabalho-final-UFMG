package br.com.ufmg.coltec.locamais.filemanager;

import br.com.ufmg.coltec.locamais.model.HistoricoLocacao;

import java.io.IOException;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.*;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.HISTORICO_LOCACAO_DIRECTORY;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.HISTORICO_LOCACAO_JSON;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getAsJson;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getHistoricaoLocacaoFromJson;

/**
 * Created by elias on 10/11/18.
 */
public class HistoricoLocacaoFileManager {

    /**
     * @Metodo que recebe por parametro uma lista de históricos de locações, transforma
     * esses dados em uma String do tipo json e salva no arquivo.
     * */
    public static void saveHistoricoLocacao(List<HistoricoLocacao> historicoLocacaoList) throws IOException {
        createFolder(HISTORICO_LOCACAO_DIRECTORY);

        writeFile(HISTORICO_LOCACAO_JSON, getAsJson(historicoLocacaoList));
    }

    /**
     * @Metodo que retorna uma lista de fabricantes já cadastrados no sistema.
     * */
    public static List<HistoricoLocacao> getHistoricoLocacaoFromJsonFile() {
        String fileEntry = getFileEntry(HISTORICO_LOCACAO_JSON);
        return getHistoricaoLocacaoFromJson(fileEntry);
    }
}
