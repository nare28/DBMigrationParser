function selectMigDbs(formObj) {
	var srcTgt = formObj.dbmigtypes.value.split('-');
	document.getElementById("srcDB").innerHTML=srcTgt[0];
	document.getElementById("tgtDB").innerHTML=srcTgt[1];
}

function translateQuery(formObj) {
	// Validate Input Query
	formObj.target.value = '';
	let srcQuery = formObj.source.value;
	if(srcQuery == null || srcQuery.trim().length == 0) {
		alert("Please enter SQL Query !!!");
		formObj.reset();
		return false;
	}
	// Sanitize Query by removinh the tabs, new lines and spaces
	var stringTokens = [];
	srcQuery = sanitizeQuery(stringTokens, srcQuery);
	
	var srcTgt = formObj.dbmigtypes.value.split('-');
	var jsonArray = JSON.parse(queryTrans); 
	var mappers;
	for (index in jsonArray) {
  		if(jsonArray[index].source == srcTgt[0] && jsonArray[index].target == srcTgt[1]) {
  			mappers = jsonArray[index].mappers;
  			break;
  		}
	} 
	if(mappers == null) {
		alert("Mapper does not exist for the selected source and target DBs !");
		return false;
	}
	
	var srcPatter = null;
	var tgtPatter = null;
	
	// Check for matching pattern
	var index = 0;
	let currValTokens = [];
	let prevValTokens = [];
	console.log("srcQuery="+srcQuery);
	let prevTokens = 0;
	
	for (;mappers[index]; index++) {
		srcPatter = mappers[index].src;
		console.log("Source Patter = "+srcPatter);
		tokens = srcPatter.split(' ');
		let tempQuery =  srcQuery;
		let startIndex = -1;
		tknIndex = -1;
		let prevKeyLength = 0;
		
		currValTokens = []; // Reset Data
		var i = 0;
		
		for (; tokens[i]; i = i + 2) {
			if(tokens[i].indexOf('(') == 0) {
				var options = tokens[i].substring(1, tokens[i].indexOf(')')).split('|');
				for(var j=0; options[j]; j++) {
					startIndex = tempQuery.indexOf(options[j]); 
					if(startIndex > -1) {
						var optKey =  tokens[i].substring(tokens[i].indexOf('::') + 2);
						tknIndex = optKey.substring(9);
						currValTokens[tknIndex] = options[j];
						tokens[i] = options[j];
						break;
					}
				}
				console.log(tokens[i] +"<----->"+optKey);
			} else {
				startIndex = tempQuery.indexOf(tokens[i]); 
			}
			
			if(startIndex > -1) { // Check Keyword Exists	
				if(prevKeyLength < 1) {
					tempQuery = tempQuery.substring(startIndex + tokens[i].length) 
					prevKeyLength = tokens[i].length;
					continue;
				}
				if(tokens[i-1].startsWith('gimbd_tkn')) {
					tknIndex = tokens[i-1].substring(9);
					if(currValTokens[tknIndex] == null) {
						currValTokens[tknIndex] = tempQuery.substring(0, startIndex);
					} else {
						if(currValTokens[tknIndex].trim() != tempQuery.substring(0, startIndex).trim()) {
							break;
						}
					}
					tempQuery = tempQuery.substring(startIndex + tokens[i].length);	
					prevKeyLength = tokens[i].length;
				} else {
					break;
				}
			} else {
				break;
			}
		} // End Of Token Loop
		
		console.log("PrevTokens = "+prevTokens+", CurrentTokens = "+i);
		if(tokens.length == i && prevTokens < i) {
			prevTokens = i;
			tknIndex = tokens[i-1].substring(9);
			currValTokens[tknIndex] = tempQuery;
			prevValTokens = currValTokens;
			tgtPatter = mappers[index].tgt;
			console.log("Relevant Patter = "+tgtPatter);
		}
	} // End of Pattern Loop
	
	if(tgtPatter == null) {
		alert("No matching query pattern !");
		return false;	
	}
	
	newQuery = tgtPatter;
	for (var i = 0; i < prevValTokens.length; i++) {
		if(prevValTokens[i] == null)
			continue;
		newQuery = newQuery.replace('gimbd_tkn'+i, prevValTokens[i]);
	}
	
	for (var i = 0; stringTokens[i]; i++) {
		newQuery = newQuery.replace('rts_nkt_'+i, '\''+stringTokens[i]+'\'');
	}
	
	// Add Newlines and Tabs
	newQuery = newQuery.replace('<dnl>', '\n');
	newQuery = newQuery.replace('<dnl>', '\n');
	newQuery = newQuery.replace('<dnl>', '\n');
	newQuery = newQuery.replace('<dnl>', '\n');
	
	newQuery = newQuery.replace('<dnt>', '\t');
	newQuery = newQuery.replace('<dnt>', '\t');
	
	newQuery = newQuery.replace('INNER_JOIN ', 'INNER JOIN ');
	newQuery = newQuery.replace('LEFT_OUTER_JOIN ', 'LEFT OUTER JOIN ');
	newQuery = newQuery.replace('RIGHT_OUTER_JOIN ', 'RIGHT OUTER JOIN ');
	newQuery = newQuery.replace(' NOT_IN ', ' NOT IN ');
	newQuery = newQuery.replace(' NOT_IN(', ' NOT IN (');
	
	formObj.target.value = newQuery;
}

function sanitizeQuery(stringTokens, queryString) {
	console.log(queryString);
	var srcQuery = queryString.replace(/([\n\r\t\s]+)/g, ' ');
	srcQuery = srcQuery.replace(' INNER JOIN ', ' INNER_JOIN ');
	srcQuery = srcQuery.replace(' LEFT OUTER JOIN ', ' LEFT_OUTER_JOIN ');
	srcQuery = srcQuery.replace(' RIGHT OUTER JOIN ', ' RIGHT_OUTER_JOIN ');
	srcQuery = srcQuery.replace(' NOT IN ', ' NOT_IN ');
	srcQuery = srcQuery.replace(' NOT IN(', ' NOT_IN (');
	srcQuery = srcQuery.replace('DELETE FROM ', 'DELETE_FROM ');
	
	
	tokens = srcQuery.split('');
	var start = false;
	var tokenIndex = -1;
	var newQuery = '';
	
	// Extract String values for Query
	for(var i = 0; i < tokens.length; i++) {
		if(tokens[i] == '\'') {
			if(start) {
				start = false;
			} else {
				start = true;
				tokenIndex++;
				stringTokens[tokenIndex] = '';
				newQuery = newQuery + 'rts_nkt_' + tokenIndex;
			}	
		} else if(start) {
			stringTokens[tokenIndex] = stringTokens[tokenIndex] + tokens[i];
		} else {
			newQuery = newQuery + tokens[i];
		}	
	}
	
	return newQuery;
}