package Ex_mmhotel;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    // ⭐️ 추가된 부분 1: Filter 초기화 시 호출되는 init 메소드
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 특별한 초기화 작업이 없으므로 내용은 비워둡니다.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    // ⭐️ 추가된 부분 2: Filter 종료 시 호출되는 destroy 메소드
    @Override
    public void destroy() {
        // 특별한 종료 작업이 없으므로 내용은 비워둡니다.
    }
}
