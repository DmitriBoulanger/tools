package de.dbo.tools.maven.project;


public final class PomFilter {

    private String groupPlus     = null;
    private String groupMinus    = null;
    private String artifactMinus = null;
    private String artifactPlus  = null;

    public PomFilter() {

    }

    public String getGroupPlus() {
        return groupPlus;
    }

    public String getGroupMinus() {
        return groupMinus;
    }

    public String getArtifactMinus() {
        return artifactMinus;
    }

    public String getArtifactPlus() {
        return artifactPlus;
    }

    public void setGroupPlus(String groupPlus) {
        this.groupPlus = groupPlus;
    }

    public void setGroupMinus(String groupMinus) {
        this.groupMinus = groupMinus;
    }

    public void setArtifactMinus(String artifactMinus) {
        this.artifactMinus = artifactMinus;
    }

    public void setArtifactPlus(String artifactPlus) {
        this.artifactPlus = artifactPlus;
    }

    public boolean isGroupPlus(final String group) {
        if (null == groupPlus) {
            return true;
        }
        return -1 != group.indexOf(groupPlus);
    }

    public boolean isGroupMinus(final String group) {
        if (null == groupMinus) {
            return false;
        }
        return -1 != group.indexOf(groupMinus);
    }

    public boolean isArtifactPlus(final String artifact) {
        if (null == artifactPlus) {
            return true;
        }
        return -1 != artifact.indexOf(artifactPlus);
    }

    public boolean isArtifactMinus(final String artifact) {
        if (null == artifactMinus) {
            return false;
        }
        return -1 != artifact.indexOf(artifactMinus);
    }

    public boolean isPlus(final Pom pom) {
        return isPlus(pom.id());
    }

    public boolean isMinus(final Pom pom) {
        return isMinus(pom.id());
    }

    public boolean isPlus(final PomId pomId) {
        return isGroupPlus(pomId.getGroup()) && isArtifactPlus(pomId.getArtifact());
    }

    public boolean isMinus(final PomId pomId) {
        return isGroupMinus(pomId.getGroup()) && isArtifactMinus(pomId.getArtifact());
    }

    public StringBuffer print() {
        final StringBuffer sb = new StringBuffer();
        if (null == groupMinus && null == groupPlus && null == artifactMinus && null == artifactPlus) {
            return new StringBuffer("[]");
        }

        if (null != groupMinus || null != groupPlus) {
            sb.append(" group: " + n(groupPlus, '+') + " " + n(groupMinus, '-'));
        }
        if (null != artifactMinus || null != artifactPlus) {
            sb.append(" artifact: " + n(artifactPlus, '+') + " " + n(artifactMinus, '-'));
        }
        return sb;
    }

    private static final String n(final String x, final char sign) {
        return null == x ? "" : sign + "[" + x.trim() + "]";
    }

}
