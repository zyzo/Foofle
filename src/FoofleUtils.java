public class FoofleUtils {
    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s.trim()
                // Replace no-break space by normal space
                .replaceAll("(^\\h*)|(\\h*$)",""));
    }

    public static void main(String[] args) {
        System.out.println(isNullOrEmpty("      "));
    }
}
