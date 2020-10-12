package org.shipkit.auto.version;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.regex.Pattern;

import static java.lang.Integer.max;

/**
 * Counts commits based on the output from 'git log'
 */
class CommitCounter {
    private final static Logger LOG = Logging.getLogger(CommitCounter.class);

    /**
     * Counts merge commits based on git output. Use cases:
     *
     * <p>
     *
     * a) 2 merge commits - result: 2
     * <pre>
     *   merge commit a
     *      commit a.1
     *      commit a.2
     *   merge commit b
     *      commit b.1
     *      commit b.2
     * </pre>
     *
     * b) commit on top of merge commit - result: 3
     * <pre>
     *   commit x
     *   merge commit a
     *      commit a.1
     *   merge commit b
     *      commit b.1
     * </pre>
     *
     * c) no merge commits - result: 2
     * <pre>
     *   commit 1
     *   commit 2
     * </pre>
     *
     * d) no commits - result: 1 - this way it's easier for consumers
     *
     * @param gitOutput - output from "git log --pretty=oneline" command
     */
    int countCommitDelta(String gitOutput) {
        LOG.lifecycle("countCommitDelta " + gitOutput);
        gitOutput = gitOutput.trim();
        String[] lines = gitOutput.split("\\R");
        Pattern pattern = Pattern.compile(".* Merge pull request #\\d+ from .*");
        int commits = 0;
        int mergeCommits = 0;
        for (int i = lines.length-1; i >= 0; i--) {
            String line = lines[i];
            if(line.trim().length() != 0) {
                commits++;
                if (pattern.matcher(line).matches()) {
                    mergeCommits++;
                    commits = 0; //reset so that we focus on merge commits
                }
            }
        }
        return mergeCommits + commits;
    }
}
