$('.message a').click(function(){
   $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
});

function valid_password(form){
    if(form.passwd.value == form.passwd_confirm.value){
        return true;
    }
    form.passwd_confirm.focus();
    alert("different password. check the password validation.");
    return false;
}

$('#passwd').on('blur', function(){
    if(this.value.length < 8){ // checks the password value length
       alert('You have entered less than 8 characters for password');
       $(this).focus(); // focuses the current field.
       return false; // stops the execution.
    }
});

$('#username').on('blur', function(){
    if(this.value.length < 2){ // checks the password value length
       alert('You have entered less than 2 characters for username');
       $(this).focus(); // focuses the current field.
       return false; // stops the execution.
    }
});