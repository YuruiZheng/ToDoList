package commons;

public enum DataType {
    BOARD("/board/"), CARD("/card/"), LIST("/list/"),
    SUBTASK("/subtask/"), TAG("/tag/"), COLORPRESET("/colorpreset/");
    private String webSocketPath;

    DataType(String webSocketPath) {
        this.webSocketPath = webSocketPath;
    }

    public String getWebSocketPath() {
        return webSocketPath;
    }
}
