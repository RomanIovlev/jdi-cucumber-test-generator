function cancelCaseEditing() {
    $("#case-description-textfield").val('');
    $("#case-priority-textfield").val('');
    $("#case-stepsnum-textfield").val('');
    $("#code-textarea").val('');
}

function saveCase() {
    var description = $("#case-description-textfield").val();
    var priority = $("#case-priority-textfield").val();
    var code = $("#code-textarea").val();
    var suit_id = $("#suitId").text();
    var case_id = $("#caseId").text();

    alert(description);
    alert(priority);
    alert(code);
    alert(case_id);

    if (description === null || description === "" ||
        priority === null || priority === "") {
        alert("Fill in all required entry field!");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/updateCase",
        data: {
            suitID: suit_id,
            caseID: case_id,
            description: description,
            priority: priority,
            code: code
        }, // parameters
        success : function(response) {
            alert("Success");
        },
        error: function( xhr, textStatus ) {
            alert( [ xhr.status, textStatus ] );
        }
    });
}

function removeCases() {



    var description = $("#case-description-textfield").val();
    var priority = $("#case-priority-textfield").val();
    var code = $("#code-textarea").val();


    if (description === null || description === "" ||
        priority === null || priority === "") {
        alert("Fill in all required entry field!");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/saveCase",
        data: {
            suitID: suit_id,
            caseId: case_id,
            description: description,
            priority: priority,
            code: code
        }, // parameters
        success : function(response) {
            alert("Success");
        },
        error: function( xhr, textStatus ) {
            alert( [ xhr.status, textStatus ] );
        }
    });
}