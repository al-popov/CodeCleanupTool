package com.aurea.cleanuptool.source;

import one.util.streamex.StreamEx;
import java.io.IOException;
import java.nio.file.Path;

public interface SourceFinder {

    StreamEx<Path> javaClasses() throws IOException;
}
