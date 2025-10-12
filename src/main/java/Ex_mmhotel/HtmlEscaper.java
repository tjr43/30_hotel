package Ex_mmhotel;

public class HtmlEscaper {
    /**
     * HTML 특수 문자를 안전한 문자 엔티티로 변환합니다.
     * 예를 들어 '<' 문자는 '&lt;'로 변환하여 HTML 태그로 인식되지 않도록 합니다.
     *
     * @param text 변환할 원본 문자열
     * @return HTML-safe 문자열
     */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '/':
                    sb.append("&#x2F;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}
