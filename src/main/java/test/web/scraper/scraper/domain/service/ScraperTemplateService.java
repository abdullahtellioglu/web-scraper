package test.web.scraper.scraper.domain.service;

import test.web.scraper.scraper.domain.entity.ScraperTemplateEntity;
import test.web.scraper.scraper.domain.repository.ScraperTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScraperTemplateService {

    private final ScraperTemplateRepository scraperTemplateRepository;

    public ScraperTemplateService(ScraperTemplateRepository scraperTemplateRepository) {
        this.scraperTemplateRepository = scraperTemplateRepository;
    }

    public void save(ScraperTemplateEntity scraperTemplateEntity) {
        scraperTemplateRepository.save(scraperTemplateEntity);
    }
    public List<ScraperTemplateEntity> findAll(){
        return scraperTemplateRepository.findAll();
    }
}
