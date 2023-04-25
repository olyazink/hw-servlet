package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;
    final String METHOD_GET = "GET";
    final String METHOD_POST = "POST";
    final String METHOD_DELETE = "DELETE";
    final String POINT = "/api/posts";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            String path = req.getRequestURI();
            String method = req.getMethod();
            // primitive routing
            if (method.equals(METHOD_GET) && path.equals(POINT)) {
                controller.all(resp);
                return;
            }
            if (method.equals(METHOD_GET) && path.matches(POINT + "\\d+")) {
                // easy way
                Long id = pathForId(path);
                controller.getById(id, resp);
                return;
            }
            if (method.equals(METHOD_POST) && path.equals(POINT)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(METHOD_DELETE) && path.matches(POINT + "\\d+")) {
                // easy way
                Long id = pathForId(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
    private Long pathForId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/")));
    }
}

