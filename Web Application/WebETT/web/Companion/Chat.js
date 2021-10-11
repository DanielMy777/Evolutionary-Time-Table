$(function () {
    $("#chat-form").submit(function () {
        var data = $(this).serialize();
        data += "&action=add";
        $.ajax({
            data: data,
            url: this.action,
            timeout: 2000,
            error: function(errorObject) {
                console.error("Failed to send chat!");
            },
            success: function(success) {
                var inp = document.getElementById("chat-input");
                inp.value = "";
            }
        });
        return false;
    });
})

$(document).delegate(".chat-btn", "click", function() {
    var value = $(this).attr("chat-value");
    var name = $(this).html();
    $("#chat-input").attr("disabled", false);
})

$("#chat-circle").click(function() {
    $("#chat-circle").toggle('scale');
    $("#users-circle").toggle('scale');
    $("#chat-box").show('scale');
    $("#users-box").hide('scale');
    $("#chat-header").innerHTML = "Chat!"
})

$("#users-circle").click(function() {
    $("#users-circle").toggle('scale');
    $("#chat-circle").toggle('scale');
    $("#chat-box").hide('scale');
    $("#users-box").show('scale');
    $("#chat-header").innerHTML = "Online Users:"
})

$(".chat-box-toggle").click(function() {
    $("#chat-circle").toggle('scale');
    $("#users-circle").toggle('scale');
    $(".chat-box").hide('scale');
})

function createNewChatLI(writer, color, text)
{
    var newLI = document.createElement("li");
    newLI.classList.add("chat-msg");
    newLI.innerHTML = "<p class=\"cm-msg-text\"><span style='color: "+color+"'><b>"+writer+":</b></span> "+text+"</p>"
    return newLI;
}

function colorValToHexColor(value)
{
    return "#"+((value)>>>0).toString(16).slice(-6);
}

function refreshChats()
{
    var chatLog = document.getElementById("chat-log");
    $.ajax({
        data: "action=get",
        url: "../chat",
        timeout: 2000,
        error: function(errorObject) {
            console.error("Failed to fetch chat!");
        },
        success: function(chatEntries) {
            chatLog.innerHTML = "";
            $.each(chatEntries || [] , function () {
                let writer = this["writer"]["name"];
                let color = colorValToHexColor(this["writer"]["userColor"]["value"]);
                let text = this["message"];
                let newLI = createNewChatLI(writer, color, text);
                chatLog.appendChild(newLI);
            })
        }
    });
}

function createNewUserLI(name, color)
{
    var newLI = document.createElement("li");
    newLI.classList.add("chat-msg");
    newLI.innerHTML = "<p class=\"cm-msg-text\"><span style='color: "+color+"'><b>"+name+"</b></span></p>"
    return newLI;
}

function refreshUsers() {
    var userLog = document.getElementById("users-log");
    $.ajax({
        data: "action=users",
        url: "../chat",
        timeout: 2000,
        error: function(errorObject) {
            console.error("Failed to fetch chat!");
        },
        success: function(userEntries) {
            userLog.innerHTML = "";
            $.each(userEntries || [] , function () {
                let name = this["name"];
                let color = colorValToHexColor(this["userColor"]["value"]);
                let newLI = createNewUserLI(name, color);
                userLog.appendChild(newLI);
            })
        }
    });
}

$(function () {
    setInterval(refreshChats, 2000);
    setInterval(refreshUsers, 2000);
})