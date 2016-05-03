package nl.guno.collab.intellij;

import org.jetbrains.annotations.NotNull;

enum Platform {
    LINUX("linux"),
    MAC("mac"),
    WINDOWS("windows");

    private final String identifier;

    Platform(String identifier) {
        this.identifier = identifier;
    }

    @NotNull
    static Platform determine() {
        return fromIdentifier(System.getProperty("os.name"));
    }

    @NotNull
    private static Platform fromIdentifier(@NotNull String identifier) {
        for (Platform p : values()) {
            if (identifier.toLowerCase().contains(p.identifier)) return p;
        }

        throw new IllegalArgumentException("Unknown platform identifier '" + identifier + "'.");
    }

    @NotNull
    String pingCommand(@NotNull String host) {
        switch (this) {
            case WINDOWS:
                return "ping -n 1 " + host;
            case LINUX:
            case MAC:
                return "ping -c 1 " + host;
            default:
                throw new RuntimeException("Platform '" + this + "' unknown.");
        }
    }

    @NotNull
    String svnCommand() {
        return "svn --version";
    }

}
