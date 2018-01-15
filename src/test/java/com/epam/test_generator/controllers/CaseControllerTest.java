package com.epam.test_generator.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.test_generator.dto.CaseDTO;
import com.epam.test_generator.dto.EditCaseDTO;
import com.epam.test_generator.dto.SuitDTO;
import com.epam.test_generator.entities.Action;
import com.epam.test_generator.entities.Event;
import com.epam.test_generator.entities.Status;
import com.epam.test_generator.services.CaseService;
import com.epam.test_generator.services.SuitService;
import com.epam.test_generator.services.exceptions.BadRequestException;
import com.epam.test_generator.services.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class CaseControllerTest {

    private ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;
    private CaseDTO caseDTO;
    private SuitDTO suitDTO;
    private List<CaseDTO> caseDTOList;

    private List<EditCaseDTO> editCaseDTOList;

    private static final long SIMPLE_SUIT_ID = 1L;
    private static final long SIMPLE_CASE_ID = 2L;
    private static final Long[] CASE_IDS = {3L, 4L, 5L};

    @Mock
    private CaseService casesService;

    @Mock
    private SuitService suitService;

    @InjectMocks
    private CaseController caseController;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(caseController)
            .setControllerAdvice(new GlobalExceptionController())
            .build();
        caseDTO = new CaseDTO();
        caseDTO.setId(SIMPLE_CASE_ID);
        caseDTO.setName("name1");
        caseDTO.setDescription("case1");
        caseDTO.setPriority(1);
        caseDTO.setSteps(new ArrayList<>());
        caseDTO.setStatus(Status.NOT_DONE);

        final CaseDTO caseDTO1 = new CaseDTO();
        caseDTO1.setId(CASE_IDS[0]);
        caseDTO1.setDescription("case2");
        caseDTO1.setPriority(2);
        caseDTO1.setSteps(new ArrayList<>());

        final CaseDTO caseDTO2 = new CaseDTO();
        caseDTO2.setId(CASE_IDS[1]);
        caseDTO2.setDescription("case3");
        caseDTO2.setPriority(2);
        caseDTO2.setSteps(new ArrayList<>());

        final CaseDTO caseDTO3 = new CaseDTO();
        caseDTO3.setId(CASE_IDS[2]);
        caseDTO3.setDescription("case3");
        caseDTO3.setPriority(2);
        caseDTO3.setSteps(new ArrayList<>());

        suitDTO = new SuitDTO();
        suitDTO.setId(SIMPLE_SUIT_ID);
        suitDTO.setName("Suit name");
        suitDTO.setPriority(1);
        suitDTO.setDescription("Suit description");

        caseDTOList = new ArrayList<>();
        caseDTOList.add(caseDTO);
        caseDTOList.add(caseDTO1);
        caseDTOList.add(caseDTO2);
        caseDTOList.add(caseDTO3);

        suitDTO.setCases(caseDTOList);

        editCaseDTOList = new ArrayList<>();

        EditCaseDTO editCaseDTO1 = new EditCaseDTO("descr", "name", 1,
            Status.NOT_RUN, Action.CREATE);
        editCaseDTO1.setId(CASE_IDS[0]);
        EditCaseDTO editCaseDTO2 = new EditCaseDTO("descr","name", 1,
            Status.NOT_RUN, Action.UPDATE);
        editCaseDTO2.setId(CASE_IDS[1]);
        EditCaseDTO editCaseDTO3 = new EditCaseDTO("descr", "name", 1,
            Status.NOT_RUN, Action.UPDATE);
        editCaseDTO3.setId(CASE_IDS[2]);

        editCaseDTOList.add(editCaseDTO1);
        editCaseDTOList.add(editCaseDTO2);
        editCaseDTOList.add(editCaseDTO3);

        when(suitService.getSuit(anyLong())).thenReturn(suitDTO);
    }

    @Test
    public void testGetCase_return200whenGetCase() throws Exception {
        when(casesService.getCase(anyLong(), anyLong())).thenReturn(caseDTO);

        mockMvc.perform(get("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(mapper.writeValueAsString(caseDTO)));

        verify(casesService).getCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testGetCase_return404whenSuitNotExistOrCaseNotExist() throws Exception {
        when(casesService.getCase(anyLong(), anyLong())).thenThrow(new NotFoundException());

        mockMvc.perform(get("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(casesService).getCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testGetCase_return400whenSuitNotContainsCase() throws Exception {
        suitDTO.setCases(null);
        when(casesService.getCase(anyLong(), anyLong())).thenThrow(new BadRequestException());

        mockMvc.perform(get("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(casesService).getCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testGetCase_return500whenRuntimeException() throws Exception {
        when(casesService.getCase(anyLong(), anyLong())).thenThrow(new RuntimeException());

        mockMvc.perform(get("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isInternalServerError());

        verify(casesService).getCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testAddCase_return201whenAddNewCase() throws Exception {
        caseDTO.setId(null);
        when(casesService.addCaseToSuit(anyLong(), any(CaseDTO.class))).thenReturn(SIMPLE_CASE_ID);

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string(String.valueOf(SIMPLE_CASE_ID)));

        verify(casesService).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return404whenSuitNotExistOrCaseNotExist() throws Exception {
        caseDTO.setId(null);
        when(casesService.addCaseToSuit(anyLong(), any(CaseDTO.class)))
            .thenThrow(new NotFoundException());

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(casesService).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return422whenAddCaseWithNullDescription() throws Exception {
        caseDTO.setId(null);
        caseDTO.setDescription(null);

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0)).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return422whenAddCaseWithEmptyDescription() throws Exception {
        caseDTO.setId(null);
        caseDTO.setDescription("");

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0)).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return422whenAddCaseWithNullPriority() throws Exception {
        caseDTO.setId(null);
        caseDTO.setPriority(null);

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0)).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return422whenAddCaseWithZeroPriority() throws Exception {
        caseDTO.setId(null);
        caseDTO.setPriority(0);

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0)).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return422whenAddCaseWithMoreThanTheRequiredPriority() throws Exception {
        caseDTO.setId(null);
        caseDTO.setPriority(6);

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0)).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return422whenAddCaseWithLessThanTheRequiredPriority() throws Exception {
        caseDTO.setId(null);
        caseDTO.setPriority(-4);

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0)).addCaseToSuit(eq(SIMPLE_SUIT_ID), any(CaseDTO.class));
    }

    @Test
    public void testAddCase_return500whenRuntimeException() throws Exception {
        when(casesService.addCaseToSuit(anyLong(), any(CaseDTO.class)))
            .thenThrow(new RuntimeException());

        mockMvc.perform(post("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andDo(print())
            .andExpect(status().isInternalServerError());

        verify(casesService).addCaseToSuit(anyLong(), any(CaseDTO.class));
    }

    @Test
    public void testUpdateCase_return200whenUpdateCase() throws Exception {
        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(editCaseDTOList.get(0))))
            .andDo(print())
            .andExpect(status().isOk());

        verify(casesService)
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return404whenSuitNotExistOrCaseNotExist() throws Exception {
        doThrow(NotFoundException.class).when(casesService)
            .updateCase(anyLong(), anyLong(), any(EditCaseDTO.class));

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(editCaseDTOList.get(0))))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(casesService)
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return400whenSuitNotContainsCase() throws Exception {
        doThrow(BadRequestException.class).when(casesService)
            .updateCase(anyLong(), anyLong(), any(EditCaseDTO.class));

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(editCaseDTOList.get(0))))
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(casesService)
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return422whenUpdateCaseWithZeroPriority() throws Exception {
        caseDTO.setPriority(0);

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0))
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return422whenUpdateCaseWithMoreThanTheRequiredPriority()
        throws Exception {
        caseDTO.setPriority(6);

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0))
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return422whenUpdateCaseWithLessThanTheRequiredPriority()
        throws Exception {
        caseDTO.setPriority(-4);

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0))
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return422whenUpdateCaseWithEmptyDescription() throws Exception {
        caseDTO.setDescription("");

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(caseDTO)))
            .andExpect(status().isBadRequest());

        verify(casesService, times(0))
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testUpdateCase_return500whenRuntimeException() throws Exception {
        doThrow(RuntimeException.class).when(casesService)
            .updateCase(anyLong(), anyLong(), any(EditCaseDTO.class));

        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(editCaseDTOList.get(0))))
            .andDo(print())
            .andExpect(status().isInternalServerError());

        verify(casesService)
            .updateCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), any(EditCaseDTO.class));
    }

    @Test
    public void testRemoveCase_return200whenRemoveCase() throws Exception {
        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isOk());

        verify(casesService).removeCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testRemoveCase_return404whenSuitNotExistOrCaseNotExist() throws Exception {
        doThrow(NotFoundException.class).when(casesService).removeCase(anyLong(), anyLong());

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(casesService).removeCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testRemoveCase_return400whenSuitNotContainsCase() throws Exception {
        suitDTO.setCases(null);
        doThrow(BadRequestException.class).when(casesService).removeCase(anyLong(), anyLong());

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(casesService).removeCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testRemoveCase_return500whenRuntimeException() throws Exception {
        doThrow(RuntimeException.class).when(casesService).removeCase(anyLong(), anyLong());

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID))
            .andDo(print())
            .andExpect(status().isInternalServerError());

        verify(casesService).removeCase(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID));
    }

    @Test
    public void testRemoveCases_return200whenRemoveCases() throws Exception {

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(CASE_IDS)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(casesService).removeCases(eq(SIMPLE_SUIT_ID), eq(Arrays.asList(CASE_IDS)));
    }

    @Test
    public void testRemoveCases_return404whenSuitNotExistOrCaseNotExist() throws Exception {
        doThrow(NotFoundException.class).when(casesService).removeCases(anyLong(),
            anyListOf(Long.class));

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(CASE_IDS)))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(casesService).removeCases(eq(SIMPLE_SUIT_ID), eq(Arrays.asList(CASE_IDS)));
    }

    @Test
    public void testRemoveCases_return400whenSuitNotContainsCase() throws Exception {
        doThrow(BadRequestException.class).when(casesService)
            .removeCases(anyLong(), anyListOf(Long.class));

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(CASE_IDS)))
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(casesService).removeCases(eq(SIMPLE_SUIT_ID), eq(Arrays.asList(CASE_IDS)));
    }

    @Test
    public void testRemoveCases_return500whenRuntimeException() throws Exception {
        doThrow(RuntimeException.class).when(casesService)
            .removeCases(anyLong(), anyListOf(Long.class));

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(CASE_IDS)))
            .andDo(print())
            .andExpect(status().isInternalServerError());

        verify(casesService).removeCases(eq(SIMPLE_SUIT_ID), eq(Arrays.asList(CASE_IDS)));
    }

    @Test
    public void testRemoveCases_suitWithoutCasesToBeRemoved() throws Exception {

        Long[] invalidCaseIds = {6L, 8L};

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invalidCaseIds)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testRemoveCases_duplicatedCaseIds() throws Exception {

        Long[] invalidCaseIds = {3L, 4L, 4L};

        mockMvc.perform(delete("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invalidCaseIds)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(casesService).removeCases(eq(SIMPLE_SUIT_ID), eq(Arrays.asList(invalidCaseIds)));
    }

    @Test
    public void testPerformEvent_return200() throws Exception {

        mockMvc
            .perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID + "/events/CREATE")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(casesService)
            .performEvent(eq(SIMPLE_SUIT_ID), eq(SIMPLE_CASE_ID), eq(Event.CREATE));
    }

    @Test
    public void testPerformEvent_return400whenWrongEventName() throws Exception {

        mockMvc
            .perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID + "/events/WRONG")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPerformEvent_return400whenEventCantBePerformed() throws Exception {

        doThrow(BadRequestException.class).when(casesService)
            .performEvent(anyLong(), anyLong(), eq(Event.PASS));

        mockMvc
            .perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases/" + SIMPLE_CASE_ID + "/events/PASS")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCases_CorrectActions_StatusOk() throws Exception {
        mockMvc.perform(put("/suits/" + SIMPLE_SUIT_ID + "/cases")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(editCaseDTOList)))
            .andExpect(status().isOk());

        verify(casesService).updateCases(eq(SIMPLE_SUIT_ID), eq(editCaseDTOList));
    }
}