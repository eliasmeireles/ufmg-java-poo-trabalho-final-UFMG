package br.com.ufmg.coltec.locamais.filemanager;

import java.io.File;

/**
 * Created by elias on 10/11/18.
 */
public interface SystemFolderAndFilesURL {

    String MAIN_FOLDER = System.getProperty("user.home")
            .concat(File.separator.concat("Loca Mais").concat(File.separator));

    String FABRICANTES_DIRECTORY = MAIN_FOLDER.concat("fabricantes".concat(File.separator));
    String FABRICANTES_JSON = FABRICANTES_DIRECTORY.concat("fabricantes.json");

    String VEICULOS_DIRECTORY = MAIN_FOLDER.concat("veiculos".concat(File.separator));
    String VEICULOS_JSON = VEICULOS_DIRECTORY.concat("veiculos.json");
    String VEICULOS_IMAGE_DIRECTORY = VEICULOS_DIRECTORY.concat("images".concat(File.separator));

    String FUNCIONARIOS_DIRECTORY = MAIN_FOLDER.concat("funcionarios".concat(File.separator));
    String FUNCIONARIOS_IMAGE_DIRECTORY = FUNCIONARIOS_DIRECTORY.concat("images".concat(File.separator));
    String FUNCIONARIOS_JSON = FUNCIONARIOS_DIRECTORY.concat("funcionarios.json");

    String CLIENTES_DIRECTORY = MAIN_FOLDER.concat("clientes").concat(File.separator);
    String CLIENTES_IMAGE_DIRECTORY = CLIENTES_DIRECTORY.concat("images").concat(File.separator);
    String CLIENTES_JSON = CLIENTES_DIRECTORY.concat("clientes.json");

    String LOCACAO_DIRECTORY = MAIN_FOLDER.concat("locações").concat(File.separator);
    String LOCACAO_JSON = LOCACAO_DIRECTORY.concat("locacoes.json");

    String HISTORICO_LOCACAO_DIRECTORY = LOCACAO_DIRECTORY.concat("historico").concat(File.separator);
    String HISTORICO_LOCACAO_JSON = HISTORICO_LOCACAO_DIRECTORY.concat("historico.json");

}
