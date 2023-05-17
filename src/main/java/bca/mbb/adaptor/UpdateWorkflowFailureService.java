package bca.mbb.adaptor;

import bca.mbb.repository.FoTransactionHeaderRepository;
import bca.mbb.util.CommonUtil;
import lib.fo.entity.FoTransactionHeaderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateWorkflowFailureService {

    private final FoTransactionHeaderRepository foTransactionHeaderRepository;

    /*
     update workflow failure fo_transaction_header by streamTransactionId
    * */
    public void updateWorkflowFailure(FoTransactionHeaderEntity foTransactionHeader) {
        if (!CommonUtil.isNullOrEmpty(foTransactionHeader.getWorkflowFailure())) {
            log.info("update workflowfailure: {}", foTransactionHeader.getWorkflowFailure());

            String workflowFailureExisting = foTransactionHeaderRepository.getWorkflowFailure(foTransactionHeader.getChainingId());

            if (!CommonUtil.isNullOrEmpty(workflowFailureExisting)) {

                Set<String> workflowFailures = Arrays.stream(workflowFailureExisting.split(","))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                workflowFailures.add(foTransactionHeader.getWorkflowFailure());
                String updatedWorkflowFailure = String.join(",", workflowFailures);
                foTransactionHeaderRepository.updateWorkflowFailure(updatedWorkflowFailure, foTransactionHeader.getChainingId());
            } else {
                foTransactionHeaderRepository.updateWorkflowFailure(foTransactionHeader.getWorkflowFailure(), foTransactionHeader.getChainingId());
            }
        }
    }}
