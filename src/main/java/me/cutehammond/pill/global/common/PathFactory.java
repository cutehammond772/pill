package me.cutehammond.pill.global.common;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Path Name 을 한 곳에서 관리하기 위해 사용됩니다.
 * */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathFactory {

    /**
     * 클라이언트 단의 인덱스입니다. 이후 배포 시 수정되어야 합니다.
     */
    public static final PathFactory FRONT_PATH = new PathFactory("http://localhost:3000/");

    public static final PathFactory AUTH = new PathFactory("/auth");
    public static final PathFactory API = new PathFactory("/api");

    public static final PathFactory INDEX = new PathFactory("/");
    public static final PathFactory ALL = new PathFactory("/**");

    @NonNull
    private final String path;

    public PathFactory all() {
        return new PathFactory(merge("**"));
    }

    /**
     * 적절한 request 예시: <br>
     * 1. /request (o) <br>
     * 2. /re-quest (o) --> request 이름에는 알파벳(소문자)과 -(dash)만 허용됩니다. <br><br>
     *
     * 잘못된 request 예시: <br>
     * 1. /Request101 (x) <br>
     * 2. /request/request/... (x) --> 하나의 request name 만 허용됩니다. <br>
     * 위와 같은 path 를 만들려면 method chain 을 이용하세요. <br><br>
     *
     * 기타: '/request' 'request' 둘 다 사용 가능합니다.
     * */
    public PathFactory request(@NonNull String request) {
        if (!request.matches("^/?[a-z\\-]+$"))
            throw new IllegalArgumentException("Invalid path name.");

        return new PathFactory(merge(request));
    }

    private String merge(String nextPath) {
        StringBuilder builder = new StringBuilder(path);

        if (path.charAt(path.length() - 1) == '/' && nextPath.charAt(0) == '/') {
            builder.append(nextPath.substring(1));
        } else if (path.charAt(path.length() - 1) != '/' && nextPath.charAt(0) != '/') {
            builder.append("/").append(nextPath);
        } else {
            builder.append(nextPath);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return path;
    }

    public String path() {
        return path;
    }
}
