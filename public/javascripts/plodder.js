
var context="/plodder";

marked.setOptions({
	highlight : function(code) {
		return hljs.highlightAuto(code).value;
	}
});

function getWsdlPage(wsdlUrl) {
	jQuery.ajax({
		type : "GET",
		url : context+"/rawwsdl?wsdl=" + wsdlUrl,
		success : function(data) {
			$('#wsdl').html(data);
		},
		error : function(response) {
			alert("error " + response.statusText + ":" + response.status
					+ " - " + response.responseText)
		}
	});
}

function xmlToString(xmlData) {

	var xmlString;
	//IE
	if (window.ActiveXObject) {
		xmlString = xmlData.xml;
	}
	// code for Mozilla, Firefox, Opera, etc.
	else {
		xmlString = (new XMLSerializer()).serializeToString(xmlData);
	}
	return xmlString;
}

function tryit(id) {

	var request = $("#" + id).val();
	var end = $("#endpoint").val();

	jQuery.ajax({
		type : "POST",
		contentType : "application/xml; charset=utf-8",
		beforeSend : function(request) {
			request.setRequestHeader("SOAPAction", '""');
		},
		data : request,
		dataType : "xml",
		url : end,
		success : function(data) {
			restresponse = data;
			$('#tryitResponseText').val(xmlToString(data));
			$('#tryitResponseModal').modal();
		},
		error : function(response) {
			txt = "error " + response.statusText + "\n" + response.status + "\n" + response.responseText;
			$('#tryitResponseText').val(txt);
			$('#tryitResponseModal').modal();
		}

	});
}

function jsonEscape(str) {
	return str.replace(/\n/g, "\\\\n").replace(/\r/g, "\\\\r").replace(/\t/g,
			"\\\\t").replace(/\"/g, '\\"').replace(/'/g, "\\\\'");
}
function unescapeJson(str) {
	return str.replace(new RegExp("\\\\n", 'g'), "\n").replace(
			new RegExp("\\\\r", 'g'), "\r").replace(new RegExp("\\\\t", 'g'),
			"\t").replace(new RegExp("\\\\'", 'g'), "'").replace(
			new RegExp('\\"', 'g'), '"');
}

var restresponse = "";
function getPage() {

	var path = (context+"/pages/" + $('#pagepath').val()).replace(/\/\//g,"/");

	console.log("getPage "+path);
	
	jQuery.ajax({
		type : "GET",

		dataType : "json",
		url : path ,
		success : function(data) {
			restresponse = data;
			$('#text-input').val(unescapeJson(data.data));
			editorUpdate();
		},
		error : function(response) {
			alert("error " + response.statusText + ":" + response.status
					+ " - " + response.responseText)
		}

	});
}

function postPage(action) {
	
	var path = (context+"/pages/" + $('#pagepath').val()).replace(/\/\//g,"/");
	var json = '{"data":"' + jsonEscape($('#text-input').val()) + '"}'
	console.log("postPage " + path);
	jQuery.ajax({
		type : action,
		contentType : "application/json; charset=utf-8",
		data : json,
		dataType : "json",
		url : path,
		success : function(data) {
			restresponse = data;
			$('#text-input').val(unescapeJson(data.data));
			editorUpdate();
			if ( action == "POST" ) {
				listDirectories();
			}
		},
		error : function(response) {
			alert("error " + response.statusText + ":" + response.status
					+ " - " + response.responseText)
		}

	});
}

function editorUpdate() {
	document.getElementById('preview').innerHTML = marked($("#text-input")
			.val());
}

function decodeHtml(html) {
	var txt = document.createElement("textarea");
	txt.innerHTML = html;
	return txt.value;
}

function loadMarkdown(text) {
	var t = decodeHtml(text);
	document.getElementById('preview').innerHTML = marked(t);
}

function getFilesForDirectory(dir, row) {
	console.log("getFilesForDirectory " + dir);

	return jQuery
			.ajax({
				type : "GET",
				url : context+"/path/" + dir,
				success : function(data) {
					row.links = "";
					for (r in data) {
						f = dir + "/" + data[r].filename;
						f = f.replace(/\/\//g,"/");
						display=("/"+f).replace(/\/\//g,"/");
						row.links = row.links
								+ '<a href="#" class="list-group-item" onClick="openEdit(\''
								+ f + '\');">'+display+'</a>';
					}
					//$.tmpl("dirTemplate", data).appendTo("#accordion");
				},
				error : function(response) {
					alert("error " + response.statusText + ":"
							+ response.status + " - " + response.responseText)
				}
			});
}

function openEdit(f) {
	if (f != "") {
		var path = ("/" +f).replace(/\/\//g,"/");
		$('#pagepath').val(path);
	}
	editorUpdate();
	if (f != "")
		getPage();

	$('#menuEdit').removeClass("active");
	$('#menuNavigate').removeClass("active");
	$('#menuWsdl').removeClass("active");

	$('#menuEdit').addClass("active");
	$('#navigate').hide();
	$('#editPage').show();
	$('#wsdl').hide();

}
function openNavigate() {
	$('#menuEdit').removeClass("active");
	$('#menuNavigate').removeClass("active");
	$('#menuNavigate').addClass("active");
	$('#menuWsdl').removeClass("active");

	$('#navigate').show();
	$('#editPage').hide();
	$('#wsdl').hide();
}

function openWsdl() {
	$('#menuEdit').removeClass("active");
	$('#menuNavigate').removeClass("active");
	$('#menuWsdl').removeClass("active");
	$('#menuWsdl').addClass("active");
	$('#navigate').hide();
	$('#editPage').hide();
	$('#wsdl').show();
}

function listDirectories() {

	//$('#editPage').hide();

	var path = '%'

	waitFor = new Array();
	jQuery.ajax({
		type : "GET",
		data : {
			"pathfilter" : path
		},
		url : context+"/path",
		success : function(data) {
			for (r in data) {
				data[r].id = r;
				data[r].display = ("/"+data[r].path).replace(/\/\//g,"/");
				waitFor.push(getFilesForDirectory(data[r].path, data[r]))
			}

			$.when.apply(null, waitFor).done(function(x) {
				console.log("ok?? " + x);
				$('#accordion').html("")
				$.tmpl("dirTemplate", data).appendTo("#accordion");
			});
		},
		error : function(response) {
			alert("error " + response.statusText + ":" + response.status
					+ " - " + response.responseText)
		}
	});
}

var markup = '<div class="panel panel-default">'
		+ '<div class="panel-heading" role="tab" id="heading${id}">'
		+ '  <h5 class="panel-title">'
		+ '    <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapse${id}" aria-expanded="false" aria-controls="collapse${id}">'
		+ '      ${display}'
		+ ' </a>'
		+ '  </h5>'
		+ '</div>'
		+ '<div id="collapse${id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading${id}">'
		+ '<div class="list-group">' + ' {{html links}}' + '</div>' + '</div>'
		+ '</div>'

$.template("dirTemplate", markup);
