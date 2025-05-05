package test.web.scraper.scraper.domain.component;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentTemplateExtractor {
    private DocumentTemplateExtractor(){

    }

    public static List<CssSelectorAndHtml> extract(String url) throws IOException {
        Document document = Jsoup.connect(url).timeout(10 * 1000).get();;
        Element body = document.body();
        Set<String> occurrences = new HashSet<>();
        List<CssSelectorAndHtml> cssSelectorAndHtmlList = new ArrayList<>();
        for (Element element : body.getAllElements()) {
            String cssPath = getCssPath(element);
            String text = element.text();
            if(!text.isEmpty()){
                if(!occurrences.contains(text)){
                    occurrences.add(text);
                    cssSelectorAndHtmlList.add(new CssSelectorAndHtml(cssPath, text));
                }

            }
        }

        return cssSelectorAndHtmlList;
    }


    private static String getCssPath(Element element) {
        StringBuilder path = new StringBuilder();

        while (element != null && !element.tagName().equals("body")) {
            StringBuilder selector = new StringBuilder(element.tagName());

            // Add ID if available
            if (element.id() != null && !element.id().isEmpty()) {
                selector.append("#").append(element.id());
                path.insert(0, " > " + selector);
                break; // ID is unique, no need to go further up
            }

            // Add class(es) if available
            if (!element.className().isEmpty()) {
                for (String cls : element.classNames()) {
                    selector.append('.').append(cls);
                }
            }

            // Add nth-child index to make it unique
            Element parent = element.parent();
            if (parent != null) {
                int index = 1;
                for (Element sibling : parent.children()) {
                    if (sibling.equals(element)) {
                        break;
                    }
                    if (sibling.tagName().equals(element.tagName())) {
                        index++;
                    }
                }
                selector.append(":nth-of-type(").append(index).append(")");
            }

            path.insert(0, " > " + selector);
            element = element.parent();
        }

        return !path.isEmpty() ? path.substring(3) : ""; // remove leading " > "
    }


    public record CssSelectorAndHtml(String cssSelector, String html) {}
}
