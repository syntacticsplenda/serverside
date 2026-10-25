# Build every mod in the repo
build:
    ./gradlew build
    find . -path '*/build/libs/*.jar' -not -name '*-sources.jar' -not -name '*-sources-dev.jar'

# Build a single mod, e.g. `just mod ihgm`
mod mod:
    ./gradlew ":{{mod}}:build"

gen:
    ./gradlew genSources

versions:
    firefox "https://fabricmc.net/develop/"

# Reset mapping to dependencies, mapping in vscode
fix:
    ./gradlew --refresh-dependencies
    ./gradlew vscode
    echo "Reload build.gradle in vscode!!!"

# Deletes the loom cache (source files)
clean-loom:
    trash .gradle/loom-cache

test:
    ./gradlew runClient

clean:
    ./gradlew clean
