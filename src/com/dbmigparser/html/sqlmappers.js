function selectMigDbs(formObj) {
	var srcTgt = formObj.dbmigtypes.value.split('-');
	document.getElementById("srcDB").innerHTML = srcTgt[0];
	document.getElementById("tgtDB").innerHTML = srcTgt[1];
}

function escapeRegExp(string){
    return string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function replaceAll(str, term, replacement) {
  	return str.replace(new RegExp(escapeRegExp(term), 'g'), replacement);
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
						currValTokens[tknIndex] = tempQuery.substring(0, startIndex).trim();
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
		
		//console.log("PrevTokens = "+prevTokens+", CurrentTokens = " + i);
		if(tokens.length == i && prevTokens < i) {
			prevTokens = i;
			tknIndex = tokens[i-1].substring(9);
			currValTokens[tknIndex] = tempQuery;
			prevValTokens = currValTokens;
			tgtPatter = mappers[index].tgt;
			//console.log("Relevant Patter = "+tgtPatter);
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
		newQuery = replaceAll(newQuery, 'gimbd_tkn'+i, prevValTokens[i]);
	}
	
	// Refill Comments with Tokens
	for (var i = 0; stringTokens[i]; i++) {
		newQuery = replaceAll(newQuery, 'rts_nkt_'+i, '\''+stringTokens[i]+'\'');
	}
	
	if(newQuery.indexOf('fun_ex<<') > 0)
		newQuery = evalFunctionExpressions(newQuery);
		
	var replacePatterns = ['INNER_JOIN ', 'INNER JOIN ', 
					'LEFT_OUTER_JOIN ', 'LEFT OUTER JOIN ',
					'RIGHT_OUTER_JOIN ', 'RIGHT OUTER JOIN ',
					'NOT_IN ', 'NOT IN ',
					'NOT_IN(', 'NOT IN ('
					];
	
	for(var i = 0; i < replacePatterns.length; i = i + 2) {
		newQuery = replaceAll(newQuery, replacePatterns[i], replacePatterns[i + 1]);
	}
	
	// Add Newlines and Tabs
	newQuery = replaceAll(newQuery, '<dnl>', '\n');
	newQuery = replaceAll(newQuery, '<dnt>', '\t');
	
	formObj.target.value = newQuery;
}

function evalFunctionExpressions(query) {
	var index = 0;
	var newQuery = '';
	var closeIndex = 0;
	
	while((index = query.indexOf('fun_ex<<')) > -1) {
		closeIndex = query.indexOf('>>');
		var parts = query.substring(index + 8, closeIndex).split(',');
		newQuery = newQuery + query.substring(0, index);
		
		if(parts[0] == 'rep') {
			newQuery = newQuery + replaceAll(parts[1], parts[2]+'.', parts[3]+'.');
		}
		
		query = query.substring(closeIndex + 2);
	}
	
	return newQuery;
}

function sanitizeQuery(stringTokens, queryString) {
	var replacePatterns = [' INNER JOIN ', ' INNER_JOIN ', 
					' LEFT OUTER JOIN ', ' LEFT_OUTER_JOIN ',
					' RIGHT OUTER JOIN ', ' RIGHT_OUTER_JOIN ',
					' NOT IN ', ' NOT_IN ',
					' NOT IN(', ' NOT_IN (',
					'DELETE FROM ', 'DELETE_FROM '
					];
	var srcQuery = queryString.replace(/([\n\r\t\s]+)/g, ' ');
	
	for(var i = 0; i < replacePatterns.length; i = i + 2) {
		srcQuery = replaceAll(srcQuery, replacePatterns[i], replacePatterns[i + 1]);
	}
	
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