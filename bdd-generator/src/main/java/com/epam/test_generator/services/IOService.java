package com.epam.test_generator.services;

import com.epam.test_generator.controllers.caze.CaseTransformer;
import com.epam.test_generator.controllers.caze.response.CaseDTO;
import com.epam.test_generator.controllers.step.response.StepDTO;
import com.epam.test_generator.controllers.suit.SuitTransformer;
import com.epam.test_generator.controllers.suit.response.SuitDTO;
import com.epam.test_generator.dao.interfaces.SuitDAO;
import com.epam.test_generator.entities.Case;
import com.epam.test_generator.entities.Suit;
import com.epam.test_generator.file_generator.FileGenerator;
import com.epam.test_generator.services.exceptions.NotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IOService {

    @Autowired
    private FileGenerator fileGenerator;

    @Autowired
    private SuitDAO suitDAO;

    @Autowired
    private CaseTransformer caseTransformer;

    @Autowired
    private SuitTransformer suitTransformer;

    /**
     * Generates file which contains suit and it's cases.
     * @param suitId id of suit to generate
     * @param caseIds list of ids of suit's cases
     * @return string generated by fileGenerator
     * @throws IOException
     */
    public String generateFile(Long suitId, List<Long> caseIds) throws IOException {
        Suit suit = suitDAO.findById(suitId).orElseThrow(NotFoundException::new);
        List<Long> casesIdsFromSuit = suit.getCases().stream()
            .map(Case::getId).collect(Collectors.toList());
        if (!casesIdsFromSuit.containsAll(caseIds)) {
            throw new NotFoundException();
        }
        List<Case> cases = suit.getCases().stream()
            .filter(aCase -> caseIds.contains(aCase.getId()))
            .collect(Collectors.toList());

        SuitDTO suitDTO = suitTransformer.toDto(suit);
        List<CaseDTO> caseDTOs = caseTransformer.toDtoList(cases).stream()
            .sorted(Comparator.comparingLong(CaseDTO::getRowNumber))
            .peek(caseDTO -> {
                List<StepDTO> orderedStepsDTOs = caseDTO.getSteps().stream()
                    .sorted(Comparator.comparingLong(StepDTO::getRowNumber))
                    .collect(Collectors.toList());
                caseDTO.setSteps(orderedStepsDTOs);
            })
            .collect(Collectors.toList());

        return fileGenerator.generate(suitDTO, caseDTOs);
    }
}
