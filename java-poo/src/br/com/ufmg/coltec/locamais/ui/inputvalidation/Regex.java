package br.com.ufmg.coltec.locamais.ui.inputvalidation;

/**
 * Created by elias on 14/11/18.
 */
public interface Regex {

    String REGEX_NOME = "^(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))" +
            "+(?:-(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+)*(?: (?:" +
            "(?:e|y|de(?:(?: la| las| lo| los))?|do|dos|da|das|del|van|von|bin|le) )?(?:(?:(?:d'|D'|O'|Mc|Mac|al-))" +
            "?(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+" +
            "|(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+(?:-(?:[\\p{Lu}" +
            "&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+)*))+(?: (?:Jr\\.|II|III|IV))?$";

    String REGEX_NOME_FABRICANTES = "(([A-Z][a-z']*)*(\\s[A-z][a-z']*)*)*";
    String REGEX_TELEFONE_FIXO = "\\([0-9]{2}\\)\\s[0-9]{4,5}-[0-9]{4}";
    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String REGEX_CELULAR = "\\([0-9]{2}\\)\\s[9]\\s[0-9]{4}-[0-9]{4}";
    String REGEX_CPF = "[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}";
    String REGEX_CEP = "[0-9]{5}-[0-9]{3}";
    String REGEX_PLACA_VEICULO = "[A-Z]{3}[0-9]{4}";

    String REGEX_DATA_STRING = "^(?:(?:31(/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(/|-|\\.)" +
            "(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(/|-|\\.)0?2\\3(?:(?:" +
            "(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)" +
            ")))$|^(?:0?[1-9]|1\\d|2[0-8])(/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

    String REGEX_SENHA = "[A-Za-z0-9]*";
    String REGEX_NOME_CARRO = "([A-Z][a-z']{2,})*(\\s[A-Z][a-z]{2,})*";

}
