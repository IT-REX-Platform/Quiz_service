package de.unistuttgart.iste.gits.quiz_service.matcher;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEmbeddable;
import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import de.unistuttgart.iste.gits.generated.dto.ResourceMarkdown;
import de.unistuttgart.iste.gits.generated.dto.ResourceMarkdownInput;

import java.util.Objects;

public class ResourceMarkdownMatchers {

    public static boolean markdownMatches(ResourceMarkdownInput input, ResourceMarkdownEntity entity) {
        return Objects.equals(
                input == null ? null : input.getText(),
                entity == null ? null : entity.getText()
        );
    }

    public static boolean markdownMatches(ResourceMarkdownEntity entity, ResourceMarkdownInput input) {
        return markdownMatches(input, entity);
    }

    public static boolean markdownMatches(ResourceMarkdown input, ResourceMarkdownEntity entity) {
        return Objects.equals(
                input == null ? null : input.getText(),
                entity == null ? null : entity.getText()
        );
    }

    public static boolean markdownMatches(ResourceMarkdownInput input, ResourceMarkdownEmbeddable embeddable) {
        return Objects.equals(
                input == null ? null : input.getText(),
                embeddable == null ? null : embeddable.getText()
        );
    }

    public static boolean markdownMatches(ResourceMarkdownEmbeddable embeddable, ResourceMarkdownInput input) {
        return markdownMatches(input, embeddable);
    }

    public static boolean markdownMatches(ResourceMarkdown input, ResourceMarkdownEmbeddable embeddable) {
        return Objects.equals(
                input == null ? null : input.getText(),
                embeddable == null ? null : embeddable.getText()
        );
    }

    public static boolean markdownMatches(ResourceMarkdown markdown, ResourceMarkdownInput input) {
        return Objects.equals(
                markdown == null ? null : markdown.getText(),
                input == null ? null : input.getText()
        );
    }
}
