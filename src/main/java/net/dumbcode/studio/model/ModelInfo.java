package net.dumbcode.studio.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelInfo {

    private final String author;
    private final int textureWidth;
    private final int textureHeight;
    private final RotationOrder order;
    private final List<CubeInfo> roots = new ArrayList<>();

    public ModelInfo(String author, int textureWidth, int textureHeight, RotationOrder order) {
        this.author = author;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.order = order;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getTextureWidth() {
        return this.textureWidth;
    }

    public int getTextureHeight() {
        return this.textureHeight;
    }

    public RotationOrder getOrder() {
        return order;
    }

    public List<CubeInfo> getRoots() {
        return this.roots;
    }

    @Override
    public String toString() {
        return "ModelInfo{" +
            "author='" + author + '\'' +
            ", textureWidth=" + textureWidth +
            ", textureHeight=" + textureHeight +
            ", order=" + order +
            ", roots=" + roots.size() +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelInfo)) return false;
        ModelInfo modelInfo = (ModelInfo) o;
        return getTextureWidth() == modelInfo.getTextureWidth() &&
            getTextureHeight() == modelInfo.getTextureHeight() &&
            Objects.equals(getAuthor(), modelInfo.getAuthor()) &&
            order == modelInfo.order &&
            Objects.equals(getRoots(), modelInfo.getRoots());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthor(), getTextureWidth(), getTextureHeight(), order, getRoots());
    }
}
