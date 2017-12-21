package com.epam.test_generator.services;

import com.epam.test_generator.dao.interfaces.CaseDAO;
import com.epam.test_generator.dao.interfaces.CaseVersionDAO;
import com.epam.test_generator.dao.interfaces.StepDAO;
import com.epam.test_generator.dao.interfaces.SuitDAO;
import com.epam.test_generator.dto.StepDTO;
import com.epam.test_generator.entities.Case;
import com.epam.test_generator.entities.Step;
import com.epam.test_generator.entities.Suit;
import com.epam.test_generator.transformers.StepTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.epam.test_generator.services.utils.UtilsService.*;
import java.util.List;

@Transactional
@Service
public class StepService {

    @Autowired
    private StepDAO stepDAO;

    @Autowired
    private CaseDAO caseDAO;

    @Autowired
    private SuitDAO suitDAO;

    @Autowired
    private CaseVersionDAO caseVersionDAO;

    @Autowired
    private StepTransformer stepTransformer;

    public List<StepDTO> getStepsByCaseId(Long suitId, Long caseId) {
        Suit suit = suitDAO.findOne(suitId);
        checkNotNull(suit);

        Case caze = caseDAO.findOne(caseId);
        checkNotNull(caze);

        caseBelongsToSuit(caze, suit);

        return stepTransformer.toDtoList(caze.getSteps());
    }

    public StepDTO getStep(Long suitId, Long caseId, Long stepId) {
        Suit suit = suitDAO.findOne(suitId);
        checkNotNull(suit);

        Case caze = caseDAO.findOne(caseId);
        checkNotNull(caze);

        caseBelongsToSuit(caze, suit);

        Step step = stepDAO.findOne(stepId);
        checkNotNull(step);

        stepBelongsToCase(step, caze);

        return stepTransformer.toDto(step);
    }

    public Long addStepToCase(Long suitId, Long caseId, StepDTO stepDTO) {
        Suit suit = suitDAO.findOne(suitId);
        checkNotNull(suit);

        Case caze = caseDAO.findOne(caseId);
        checkNotNull(caze);

        caseBelongsToSuit(caze, suit);

        Step step = stepTransformer.fromDto(stepDTO);

        step = stepDAO.save(step);
        caze.getSteps().add(step);

        caseVersionDAO.save(caze);

        return step.getId();
    }

    public void updateStep(Long suitId, Long caseId, Long stepId, StepDTO stepDTO) {
        Suit suit = suitDAO.findOne(suitId);
        checkNotNull(suit);

        Case caze = caseDAO.findOne(caseId);
        checkNotNull(caze);

        caseBelongsToSuit(caze, suit);

        Step step = stepDAO.findOne(stepId);
        checkNotNull(step);

        stepBelongsToCase(step, caze);

        stepTransformer.mapDTOToEntity(stepDTO, step);

        stepDAO.save(step);

        caseVersionDAO.save(caze);
    }

    public void removeStep(Long suitId, Long caseId, Long stepId) {
        Suit suit = suitDAO.findOne(suitId);
        checkNotNull(suit);

        Case caze = caseDAO.findOne(caseId);
        checkNotNull(caze);

        caseBelongsToSuit(caze, suit);

        Step step = stepDAO.findOne(stepId);
        checkNotNull(step);

        stepBelongsToCase(step, caze);

        caze.getSteps().remove(step);
        stepDAO.delete(stepId);

        caseVersionDAO.save(caze);
    }
}
