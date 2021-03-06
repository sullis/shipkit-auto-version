package org.shipkit.auto.version

import spock.lang.Unroll

import static VersionSpec.readVersionSpec

class VersionSpecTest extends TmpFolderSpecification {

    def "loads spec from file"() {
        expect:
        readVersionSpec(writeFile("version=1.0.*")) == "1.0.*"
    }

    def "no file"() {
        when:
        readVersionSpec(new File("missing file"))

        then:
        def e = thrown(RuntimeException)
        e.message == "Problems deducting the version automatically. Please create file 'version.properties' with a valid version spec, for example 'version=1.0.*'"
        e.cause != null
    }

    def "missing 'version' property"() {
        def f = writeFile("noversion=missing")

        when:
        readVersionSpec(f)

        then:
        def e = thrown(VersionSpec.MissingVersionKey)
        e.message == "Missing 'version' property in file: '" + f.name + "'\n" +
        "  Correct examples: 'version=1.0.*', 'version=2.10.100'"
    }

    @Unroll
    def "bad version format: #spec"() {
        def f = writeFile("version=" + spec)

        when:
        readVersionSpec(f)

        then:
        def e = thrown(VersionSpec.IncorrectVersionFormat)
        e.message == "Invalid format of 'version' property in file: '" + f.name + "'\n" +
                "  Correct examples: 'version=1.0.*', 'version=2.10.100'"
        e.cause != null

        where:
        spec << ["foo.version", "1.2", "1.2.**", "1.*.*", "1.0.0-beta.*", "1.12*"]
    }
}
