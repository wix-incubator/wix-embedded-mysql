package com.wix.mysql.store;

import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.DirectoryAndExecutableNaming;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.store.ExtractedArtifactStore;
import de.flapdoodle.embed.process.store.IDownloader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * This is a wrapper around `ExtractedArtifactStore` which deletes the temp directory BEFORE extracting
 * just in case we have left overs from last crashed run.
 */
class SafeExtractedArtifactStore extends ExtractedArtifactStore {
    private String directory;

    SafeExtractedArtifactStore(IDownloadConfig downloadConfig, IDownloader downloader, DirectoryAndExecutableNaming extraction, DirectoryAndExecutableNaming directory) {
        super(downloadConfig, downloader, extraction, directory);
        this.directory = directory.getDirectory().asFile().getAbsolutePath();
    }

    @Override
    public IExtractedFileSet extractFileSet(Distribution distribution) throws IOException {
        FileUtils.deleteDirectory(new File(directory));

        IExtractedFileSet extractedFiles = super.extractFileSet(distribution);

//        Files.createDirectory(new File(directory, "data").toPath());

        return extractedFiles;
    }
}