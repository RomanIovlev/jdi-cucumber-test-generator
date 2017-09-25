var suit_id = -1;
var case_id = -1;

$(document).ready(function () {
    $('.accordion-tabs').on('click', 'li > div', function(event) {
        if (!$(this).hasClass('is-active')) {
            event.preventDefault();
            $('.accordion-tabs').find('.is-active').removeClass('is-active');
            $(this).addClass('is-active');
        } else {
            event.preventDefault();
        }
    });

    $("#cases_table_body").on("click", "tr", function(){
        $("#cases_table_body").children("tr").removeClass("is_active");
        $(this).addClass("is_active");
        case_id = $(this).children("td.small_td").children(".particular_caseId").val();
        $("#code-textarea").val("");
        $("#case-description-textfield").val("");
        $("#case-priority-textfield").val("");
        getCaseInfo();
    });

    $("#tableCases").tablesorter({
        theme: 'blue',
        headers: {
            0: {
                sorter: false
            },
            1: {
              sorter: "text"
            },
            3: {
                sorter: false
            }
        },
        widgets: ['stickyHeaders', 'zebra'],
        widgetOptions: {
            // jQuery selector or object to attach sticky header to
            stickyHeaders_attachTo : '.cases-table-container' // or $('.wrapper')
        }
    });

});

function getSuitInfo(suitId){
    $.get("/cucumber/suit/" + suitId, function(response){
        suit_id = response.id;

        $("#suit_name_info").show();

        $("#description_info").text("Suit description:");
        $("#priority_info").text("Suit priority:");
        $("#create_date_info").text("Suit create date:");
        $("#tags_info").text("Suit tags:");
        $("#value_of_name_info").val(response.name);
        $("#value_of_description_info").val(response.description);
        $("#value_of_priority_info").val(response.priority);
        $("#value_of_create_date_info").val(response.creationDate);
        $("#value_of_tags_info").val(response.tags);
        $("#countCases").text(response.cases.length);
        $(".buttons-container").empty();
        $(".buttons-container").append(
                                        "<div class='edit-suit-button' onclick='javascript:saveSuit()'>Save</div>" +
                                        "<div class='delete-suit-button' onclick='javascript:PopUpRemoveSuit()'>" +
                                        "<img src='/cucumber/static/images/trash-icon.png' style='height: 25px; margin: -5px;'></div>"
                                         );
        $("#steps_container").empty();
        $("#cases_table_body").empty();

        for(var i = 0; i < response.cases.length; i++){
            $("#cases_table_body").append($('<tr>')
                                .append($('<td>')
                                    .addClass('small_td')
                                    .append($('<input>')
                                        .attr('type', 'checkbox')
                                    )
                                    .append($('<input>')
                                        .addClass('particular_caseId')
                                        .attr('type', 'hidden')
                                        .val(response.cases[i].id)
                                    )
                                )
                                .append($('<td>')
                                    .text(response.cases[i].description)
                                ).append($('<td>')
                                    .text(response.cases[i].priority)
                                ).append($('<td>')
                                    .text(response.cases[i].tags)
                                ).append($('<td>')
                                    .text(response.cases[i].updateDate)
                                ).append($('<td style="display: none;">')
                                    .text(response.cases[i].creationDate)
                                )
                            );
        }

        $('.tablesorter').trigger('update');
    });

    disableCaseButtons();
}

function getSuitInfoWithOutCleanCases(suitId){
    $.get("/cucumber/suit/" + suitId, function(response){
        suit_id = response.id;
        $("#countCases").text(response.cases.length);
        $("#cases_table_body").empty();

         for(var i = 0; i < response.cases.length; i++){
                    $("#cases_table_body").append($('<tr>')
                                        .append($('<td>')
                                            .addClass('small_td')
                                            .append($('<input>')
                                                .attr('type', 'checkbox')
                                            )
                                            .append($('<input>')
                                                .addClass('particular_caseId')
                                                .attr('type', 'hidden')
                                                .val(response.cases[i].id)
                                            )
                                        )
                                        .append($('<td>')
                                            .text(response.cases[i].description)
                                        ).append($('<td>')
                                            .text(response.cases[i].priority)
                                        ).append($('<td>')
                                            .text(response.cases[i].tags)
                                        ).append($('<td>')
                                            .text(response.cases[i].updateDate)
                                        ).append($('<td style="display: none;">')
                                            .text(response.cases[i].creationDate)
                                        )
                                    );
                }

        $('.tablesorter').trigger('update');
    });


    disableCaseButtons();
}

function getCaseInfo(){
    $.get("/cucumber/suit/" + suit_id + "/case/" + case_id, function(response){

        $("#suit_name_info").hide();

        $("#description_info").text("Case description:");
        $("#priority_info").text("Case priority:");
        $("#create_date_info").text("Case create date:");
        $("#update_date_info").text("Case update date:");
        $("#tags_info").text("Case tags:");
        $("#value_of_description_info").val(response.description);
        $("#value_of_priority_info").val(response.priority);
        $("#value_of_create_date_info").val(response.creationDate);
        $("#value_of_update_date_info").val(response.updateDate);
        $("#value_of_tags_info").val(response.tags);
        $("#steps_container").empty();

         $(".buttons-container").empty();
                $(".buttons-container").append(
                                                "<div class='save-case-button' onclick='saveCase()'>Save</div>" +
                                                "<div class='cancel-case-button' onclick='cancelCaseEditing()'>Cancel</div>"
                                               );

        for(var i = 0; i < response.steps.length; i++){
            $("#steps_container").append("<div class=\"sortable-step-container\">\n" +
            "                                            <div>\n" +
            "                                                <img class=\"handle-icon\" src=\"/cucumber/static/images/handle-icon.png\">\n" +
            "                                                <div style=\"margin: 0; border: 1px dotted gray; width: 620px; float: left; padding: 5px;\">\n" +
            "                                                    <div class=\"select-step-type-container\">\n" +
            "                                                        <select class=\"step-type-select-tag\">\n" +
            "                                                            <option value=\"100\" disabled selected>---</option>\n" +
            "                                                            <option value=\"0\">Given</option>\n" +
            "                                                            <option value=\"1\">When</option>\n" +
            "                                                            <option value=\"2\">Then</option>\n" +
            "                                                            <option value=\"3\">And</option>\n" +
            "                                                            <option value=\"4\">But</option>\n" +
            "                                                            <option value=\"5\">*</option>\n" +
            "                                                        </select>\n" +
            "                                                    </div>\n" +
            "                                                   <input type=\"text\" class=\"step-code-line\" value='" + response.steps[i].description + "'>\n" +
            "                                                    <div style=\"clear: both; width: 0px;\"></div>\n" +
            "                                                </div>\n" +
            "                                                <img src=\"/cucumber/static/images/addRow-icon.png\" class=\"add-step-icon\">\n" +
            "                                                <img src=\"/cucumber/static/images/deleteStep-icon.png\" class=\"delete-step-icon\">\n" +
            "                                            </div>\n" +
            "                                        </div>");

            $($(".step-type-select-tag")[i]).val(response.steps[i].type);
        }
        if(response.steps.length == 0){
            $("#steps_container").append("<div class=\"sortable-step-container\">\n" +
                            "                                            <div>\n" +
                            "                                                <img  class=\"handle-icon\" src=\"/cucumber/static/images/handle-icon.png\">\n" +
                            "                                                <div style=\"margin: 0; border: 1px dotted gray; width: 620px; float: left; padding: 5px;\">\n" +
                            "                                                    <div class=\"select-step-type-container\">\n" +
                            "                                                        <select class=\"step-type-select-tag\">\n" +
                            "                                                            <option value=\"0\" disabled selected>-----</option>\n" +
                            "                                                            <option value=\"1\">Given</option>\n" +
                            "                                                            <option value=\"2\">When</option>\n" +
                            "                                                            <option value=\"3\">Then</option>\n" +
                            "                                                            <option value=\"4\">And</option>\n" +
                            "                                                            <option value=\"5\">But</option>\n" +
                            "                                                        </select>\n" +
                            "                                                    </div>\n" +
                            "                                                   <input type=\"text\" class=\"step-code-line\">\n" +
                            "                                                    <div style=\"clear: both; width: 0px;\"></div>\n" +
                            "                                                </div>\n" +
                            "                                                <img src=\"/cucumber/static/images/addRow-icon.png\" class=\"add-step-icon\">\n" +
                            "                                                <img src=\"/cucumber/static/images/deleteStep-icon.png\" class=\"delete-step-icon\">\n" +
                            "                                            </div>\n" +
                            "                                        </div>");
            }
    });
}

function saveSuit(){
    updateSuit.updateSuit();
}