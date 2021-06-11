package br.com.ufmg.coltec.locamais.json;

import br.com.ufmg.coltec.locamais.model.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elias on 10/11/18.
 */
public class JsonAsistente {

    /**
     * @Metodo que recebe um objeto e retorna um String Json desse
     * mesmo objeto.
     * */
    public static String getAsJson(Object object) throws IOException {
        return new ObjectMapper().writeValueAsString(object);
    }

    /**
     * @Metodo que recebe uma String Json de uma lista de locações
     * e retorna uma Objeto do tipo List<Locacao>.
     * */
    public static List<Locacao> getLocacoesFromJson(String locacoesJson) {
        if(locacoesJson != null && !locacoesJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(locacoesJson, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Locacao.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * @Metodo que recebe uma String Json de uma lista de clientes
     * e retorna uma Objeto do tipo List<Cliente>.
     * */
    public static List<Cliente> getClientesFromJson(String clientesJson) {
        if(clientesJson != null && !clientesJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(clientesJson, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Cliente.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }


    /**
     * @Metodo que recebe uma String Json de uma lista de fabricantes
     * e retorna uma Objeto do tipo List<Fabricante>.
     * */
    public static List<Fabricante> getFabricantesFromJson(String fabricantesJson) {
        if(fabricantesJson != null && !fabricantesJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(fabricantesJson, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Fabricante.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * @Metodo que recebe uma String Json de uma lista de fabricantes
     * e retorna uma Objeto do tipo List<Fabricante>.
     * */
    public static List<HistoricoLocacao> getHistoricaoLocacaoFromJson(String historicaoLocacaoJson) {
        if(historicaoLocacaoJson != null && !historicaoLocacaoJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(historicaoLocacaoJson, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, HistoricoLocacao.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }


    /**
     * @Metodo que recebe uma String Json de uma lista de funcionarios
     * e retorna uma Objeto do tipo List<Funcionario>.
     * */
    public static List<Funcionario> getFuncionariosFromJson(String funcionariosJson) {
        if(funcionariosJson != null && !funcionariosJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(funcionariosJson, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Funcionario.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * @Metodo que recebe uma String Json de uma lista de veículos
     * e retorna uma Objeto do tipo List<Veiculo>.
     * */
    public static List<Veiculo> getVeiculosFromJson(String veiculosJson) {
        if(veiculosJson != null && !veiculosJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(veiculosJson, objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Veiculo.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
