package br.com.ufmg.coltec.locamais.filemanager;

import br.com.ufmg.coltec.locamais.model.Locacao;

import java.io.IOException;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.createFolder;
import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.getFileEntry;
import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.writeFile;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.*;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getAsJson;
import static br.com.ufmg.coltec.locamais.json.JsonAsistente.getLocacoesFromJson;

/**
 * Created by elias on 23/11/18.
 */
public class LocacaoFileManager {


    /**
     * @Metodo que recebe um lista de locações e a salva.
     */
    public static void salvarLocacoes(List<Locacao> locacoes) throws IOException {
        createFolder(LOCACAO_DIRECTORY);
        String asJson = getAsJson(locacoes);
        writeFile(LOCACAO_JSON, asJson);
    }

    /**
     * @Metodo que retorna uma lista de locações ativas no sistema.
     */
    public static List<Locacao> getLocacoes() {
        return getLocacoesFromJson(getFileEntry(LOCACAO_JSON));
    }

}
