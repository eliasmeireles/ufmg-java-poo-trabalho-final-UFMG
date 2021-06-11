package br.com.ufmg.coltec.locamais.converter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Created by elias on 08/11/18.
 */
public class Converter {


    /**
     * @Metodo para converter data para formato brasilieiro
     * @Exemplo: 2000-01-10 para 01/10/2000.
     * */
    public static String LocalDateBRString(LocalDate date) {
        try {
            DateTimeFormatter formatador = DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.SHORT)
                    .withLocale(new Locale("pt", "br"));
            return formatador.format(date);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Meto que converte valores do tipo double para formato de moeda Brasileira.
     * @Exemplo 2000 para R$ 2.000,00.
     * */
    public static String doubleToRealString(Double valor) {
        try {
            Locale ptBr = new Locale("pt", "BR");
            String valorString = NumberFormat.getCurrencyInstance(ptBr).format(valor);
            return valorString;

        } catch (NumberFormatException e) {
            return String.valueOf(valor);
        }
    }

    /**
     * @Metodo para converter data de string para LocalDate.
     * */
    public static LocalDate stringToLocalDate(String date) throws DateTimeParseException{
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
