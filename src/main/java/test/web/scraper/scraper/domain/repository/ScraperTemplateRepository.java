package test.web.scraper.scraper.domain.repository;

import test.web.scraper.scraper.domain.entity.ScraperTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScraperTemplateRepository extends JpaRepository<ScraperTemplateEntity, Long> {
}
