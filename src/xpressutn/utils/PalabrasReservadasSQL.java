package xpressutn.utils;

public enum PalabrasReservadasSQL
{
    SELECT("select"),
    FROM("from"),
    WHERE("where"),
    AND("and"),
    OR("or"),
    JOIN("join"),
    INNER_JOIN("inner join"),
    AS("as");

    private String palabra;

    PalabrasReservadasSQL(String palabra) {
        this.palabra = palabra;
    }

    public String getPalabraReservada() {
        return palabra;
    }
    
    public static boolean contiene(String texto) {
        for (PalabrasReservadasSQL c : PalabrasReservadasSQL.values()) {
            if (c.getPalabraReservada().equals(texto.toLowerCase()) || c.getPalabraReservada().equals(texto.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
