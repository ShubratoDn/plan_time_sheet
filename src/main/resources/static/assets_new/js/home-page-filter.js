window.onscroll = function() {myFunction()};

var header = document.getElementById("myHeader");
var header1 = document.getElementById("show-button");
var sticky = header1.offsetTop;

function myFunction() {
  if (window.pageYOffset > sticky) {
    header.classList.add("filter-stick-button");
    header.classList.remove("hide");
  } else {
    header.classList.remove("filter-stick-button");
    header.classList.add("hide");
  }
}

function showFilter(){
	document.getElementById("filter-first-div").classList.add("filter-stick-add");
	document.getElementById("filterRemove").classList.remove("hide");
}

function removeFilter(){
	document.getElementById("filter-first-div").classList.remove("filter-stick-add");
	document.getElementById("filterRemove").classList.add("hide");
}