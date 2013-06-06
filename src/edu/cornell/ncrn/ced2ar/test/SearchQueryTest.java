package edu.cornell.ncrn.ced2ar.test;

import edu.cornell.ncrn.ced2ar.SearchQuery;
import static org.junit.Assert.*;

import org.junit.Test;

public class SearchQueryTest {

	@Test
	public void testGetXquery() {
		try {
			// VARIABLE NAME BEGINNING WITH 'A'
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=variablename=a*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (starts-with(lower-case($var/@name), lower-case(\"a\")))  return $var } </dataDscr></codeBook>",
					new SearchQuery("variables", "variablename=a*", "", "")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=codebooks&where=variablename=a*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\")))]  return $var/../../docDscr/citation/titlStmt/titl",
					new SearchQuery("codebooks", "variablename=a*", "", "")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablename=a*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\")))]  return $var/../../docDscr",
					new SearchQuery("documentdescriptions", "variablename=a*",
							"", "").getXquery());

			
			// VARIABLE NAME BEGINNING WITH 'A' THAT CONTAINS 'APP'
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=variablename=a*,variabletext=*app*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and (contains(lower-case(txt), lower-case(\"app\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (starts-with(lower-case($var/@name), lower-case(\"a\"))) and (contains(lower-case($var/txt), lower-case(\"app\")))  return $var } </dataDscr></codeBook>",
					new SearchQuery("variables",
							"variablename=a*,variabletext=*app*", "", "")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=codebooks&where=variablename=a*,variabletext=*app*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and (contains(lower-case(txt), lower-case(\"app\")))]  return $var/../../docDscr/citation/titlStmt/titl",
					new SearchQuery("codebooks",
							"variablename=a*,variabletext=*app*", "", "")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablename=a*,variabletext=*app*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and (contains(lower-case(txt), lower-case(\"app\")))]  return $var/../../docDscr",
					new SearchQuery("documentdescriptions",
							"variablename=a*,variabletext=*app*", "", "")
							.getXquery());

			
			// ALL FIELDS THAT CONTAIN HOUSEHOLD
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=allfields=*household*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(contains(lower-case(@name), lower-case(\"household\")) or contains(lower-case(labl), lower-case(\"household\")) or contains(lower-case(txt), lower-case(\"household\")) or contains(lower-case(codInstr), lower-case(\"household\")) or contains(lower-case(concept), lower-case(\"household\")) or contains(lower-case(../../docDscr/citation/titlStmt/titl), lower-case(\"household\")) or contains(lower-case(../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"household\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (contains(lower-case($var/@name), lower-case(\"household\")) or contains(lower-case($var/labl), lower-case(\"household\")) or contains(lower-case($var/txt), lower-case(\"household\")) or contains(lower-case($var/codInstr), lower-case(\"household\")) or contains(lower-case($var/concept), lower-case(\"household\")) or contains(lower-case($var/../../docDscr/citation/titlStmt/titl), lower-case(\"household\")) or contains(lower-case($var/../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"household\")))  return $var } </dataDscr></codeBook>",
					new SearchQuery("variables", "allfields=*household*", "",
							"").getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=allfields=*household*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(contains(lower-case(@name), lower-case(\"household\")) or contains(lower-case(labl), lower-case(\"household\")) or contains(lower-case(txt), lower-case(\"household\")) or contains(lower-case(codInstr), lower-case(\"household\")) or contains(lower-case(concept), lower-case(\"household\")) or contains(lower-case(../../docDscr/citation/titlStmt/titl), lower-case(\"household\")) or contains(lower-case(../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"household\")))]  return $var/../../docDscr",
					new SearchQuery("documentdescriptions",
							"allfields=*household*", "", "").getXquery());

			
			// VARIABLE NAME BEGINNING WITH 'A' THAT CONTAINS 'APP' OR CONTAINS
			// 'REGION'
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=variablename=a*,variabletext=*app*|variablelabel=*region*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and (contains(lower-case(txt), lower-case(\"app\"))) or (contains(lower-case(labl), lower-case(\"region\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (starts-with(lower-case($var/@name), lower-case(\"a\"))) and (contains(lower-case($var/txt), lower-case(\"app\"))) or (contains(lower-case($var/labl), lower-case(\"region\")))  return $var } </dataDscr></codeBook>",
					new SearchQuery(
							"variables",
							"variablename=a*,variabletext=*app*|variablelabel=*region*",
							"", "").getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablename=a*,variabletext=*app*|variablelabel=*region*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and (contains(lower-case(txt), lower-case(\"app\"))) or (contains(lower-case(labl), lower-case(\"region\")))]  return $var/../../docDscr",
					new SearchQuery(
							"documentdescriptions",
							"variablename=a*,variabletext=*app*|variablelabel=*region*",
							"", "").getXquery());

			
			// VARIABLE NAME = 'APPALD' OR VARIABLE NAME = 'ACREPROP' (note the
			// upper case characters here - still should work)
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=variablename=aPPALD|VARIABLENAME=ACREPROP
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(lower-case(@name) = lower-case(\"aPPALD\")) or (lower-case(@name) = lower-case(\"ACREPROP\"))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (lower-case($var/@name) = lower-case(\"aPPALD\")) or (lower-case($var/@name) = lower-case(\"ACREPROP\"))  return $var } </dataDscr></codeBook>",
					new SearchQuery("variables",
							"variablename=aPPALD|VARIABLENAME=ACREPROP", "", "")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablename=aPPALD|VARIABLENAME=ACREPROP
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(lower-case(@name) = lower-case(\"aPPALD\")) or (lower-case(@name) = lower-case(\"ACREPROP\"))]  return $var/../../docDscr",
					new SearchQuery("documentdescriptions",
							"variablename=aPPALD|VARIABLENAME=ACREPROP", "", "")
							.getXquery());

			
			// VARIABLE NAME STARTS WITH 'A' AND VARIABLE LABEL != 'APPALACHIAN
			// REGION [GENERAL VERSION]'
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=variablename=a*,variablelabel!=Appalachian
			// region [general version]
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and not (lower-case(labl) = lower-case(\"Appalachian region [general version]\"))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (starts-with(lower-case($var/@name), lower-case(\"a\"))) and not (lower-case($var/labl) = lower-case(\"Appalachian region [general version]\"))  return $var } </dataDscr></codeBook>",
					new SearchQuery(
							"variables",
							"variablename=a*,variablelabel!=Appalachian region [general version]",
							"", "").getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablename=a*,variablelabel!=Appalachian
			// region [general version]
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and not (lower-case(labl) = lower-case(\"Appalachian region [general version]\"))]  return $var/../../docDscr",
					new SearchQuery(
							"documentdescriptions",
							"variablename=a*,variablelabel!=Appalachian region [general version]",
							"", "").getXquery());

			
			// VARIABLE NAME STARTS WITH 'A' AND VARIABLE LABEL DOES NOT CONTAIN
			// 'REGION'
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=variablename=a*,variablelabel!=*region*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and not (contains(lower-case(labl), lower-case(\"region\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (starts-with(lower-case($var/@name), lower-case(\"a\"))) and not (contains(lower-case($var/labl), lower-case(\"region\")))  return $var } </dataDscr></codeBook>",
					new SearchQuery("variables",
							"variablename=a*,variablelabel!=*region*", "", "")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablename=a*,variablelabel!=*region*
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(starts-with(lower-case(@name), lower-case(\"a\"))) and not (contains(lower-case(labl), lower-case(\"region\")))]  return $var/../../docDscr",
					new SearchQuery("documentdescriptions",
							"variablename=a*,variablelabel!=*region*", "", "")
							.getXquery());

			
			// FIRST FOUR VARIABLES FROM IPUMS STARTING WITH A
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=codebooktitle=ipumsusa,variablename=a*&limit=1-4
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(lower-case(../../docDscr/citation/titlStmt/titl) = lower-case(\"ipumsusa\")) and (starts-with(lower-case(@name), lower-case(\"a\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	subsequence(for $var in $codebook/dataDscr/var where  (lower-case($var/../../docDscr/citation/titlStmt/titl) = lower-case(\"ipumsusa\")) and (starts-with(lower-case($var/@name), lower-case(\"a\")))  return $var , 1, 4)} </dataDscr></codeBook>",
					new SearchQuery("variables",
							"codebooktitle=ipumsusa,variablename=a*", "", "1-4")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=codebooktitle=ipumsusa,variablename=a*&limit=1-4
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(lower-case(../../docDscr/citation/titlStmt/titl) = lower-case(\"ipumsusa\")) and (starts-with(lower-case(@name), lower-case(\"a\")))]  return $var/../../docDscr/citation/titlStmt/titl",
					new SearchQuery("codebooks",
							"codebooktitle=ipumsusa,variablename=a*", "", "1-4")
							.getXquery());
			// http://localhost:8080/CED2AR_Query/search?return=codebooks&where=codebooktitle=ipumsusa,variablename=a*&limit=1-4
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(lower-case(../../docDscr/citation/titlStmt/titl) = lower-case(\"ipumsusa\")) and (starts-with(lower-case(@name), lower-case(\"a\")))]  return $var/../../docDscr",
					new SearchQuery("documentdescriptions",
							"codebooktitle=ipumsusa,variablename=a*", "", "1-4")
							.getXquery());

			
			// FIRST 4 VARIABLES FROM IPUMS STARTING WITH A ORDERED BY VARIABLE
			// NAME IN DESCENDING ORDER
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=codebooktitle=ipumsusa,variablename=a*&sort=-variablename&limit=1-4
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(lower-case(../../docDscr/citation/titlStmt/titl) = lower-case(\"ipumsusa\")) and (starts-with(lower-case(@name), lower-case(\"a\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	subsequence(for $var in $codebook/dataDscr/var where  (lower-case($var/../../docDscr/citation/titlStmt/titl) = lower-case(\"ipumsusa\")) and (starts-with(lower-case($var/@name), lower-case(\"a\")))  order by $var/@name descending  return $var , 1, 4)} </dataDscr></codeBook>",
					new SearchQuery("variables",
							"codebooktitle=ipumsusa,variablename=a*",
							"-variablename", "1-4").getXquery());

			
			// RETURN CODEBOOKS WHERE THERE IS A VARIABLE LABEL CONTAINING THE
			// WORD 'HOUSE' IN ASCENDING ORDER BY CODEBOOK TITLE
			// http://localhost:8080/CED2AR_Query/search?return=codebooks&where=variablelabel=*house*&sort=codebooktitle
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(contains(lower-case(labl), lower-case(\"house\")))]  order by $var/../../docDscr/citation/titlStmt/titl   return $var/../../docDscr/citation/titlStmt/titl",
					new SearchQuery("codebooks", "variablelabel=*house*",
							"codebooktitle", "").getXquery());

			
			// RETURN DOCUMENT DESCRIPTIONS WHERE THERE IS A VARIABLE LABEL
			// CONTAINING THE
			// WORD 'HOUSE' IN ASCENDING ORDER BY THE PRODUCT DATE in DESCENDING
			// ORDER
			// http://localhost:8080/CED2AR_Query/search?return=documentdescriptions&where=variablelabel=*house*&sort=-productdate
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var  where $codebook/dataDscr/var[(contains(lower-case(labl), lower-case(\"house\")))]  order by $var/../../docDscr/citation/prodStmt/prodDate/@date descending  return $var/../../docDscr",
					new SearchQuery("documentdescriptions",
							"variablelabel=*house*", "-productdate", "")
							.getXquery());

			
			// RETURN VARIABLES WHERE ALLFIELDS CONTAIN THE WORD 'HOUSE' OR
			// 'PUBLIC' ORDER BY VARIABLE NAME DESCENDING
			// http://localhost:8080/CED2AR_Query/search?return=variables&where=allfields=*house*,allfields=*public*&sort=-variablename
			assertEquals(
					"let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook  where $codebook/dataDscr/var[(contains(lower-case(@name), lower-case(\"house\")) or contains(lower-case(labl), lower-case(\"house\")) or contains(lower-case(txt), lower-case(\"house\")) or contains(lower-case(codInstr), lower-case(\"house\")) or contains(lower-case(concept), lower-case(\"house\")) or contains(lower-case(../../docDscr/citation/titlStmt/titl), lower-case(\"house\")) or contains(lower-case(../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"house\"))) and (contains(lower-case(@name), lower-case(\"public\")) or contains(lower-case(labl), lower-case(\"public\")) or contains(lower-case(txt), lower-case(\"public\")) or contains(lower-case(codInstr), lower-case(\"public\")) or contains(lower-case(concept), lower-case(\"public\")) or contains(lower-case(../../docDscr/citation/titlStmt/titl), lower-case(\"public\")) or contains(lower-case(../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"public\")))] return		<codeBook> { $codebook/docDscr }<dataDscr> {	for $var in $codebook/dataDscr/var where  (contains(lower-case($var/@name), lower-case(\"house\")) or contains(lower-case($var/labl), lower-case(\"house\")) or contains(lower-case($var/txt), lower-case(\"house\")) or contains(lower-case($var/codInstr), lower-case(\"house\")) or contains(lower-case($var/concept), lower-case(\"house\")) or contains(lower-case($var/../../docDscr/citation/titlStmt/titl), lower-case(\"house\")) or contains(lower-case($var/../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"house\"))) and (contains(lower-case($var/@name), lower-case(\"public\")) or contains(lower-case($var/labl), lower-case(\"public\")) or contains(lower-case($var/txt), lower-case(\"public\")) or contains(lower-case($var/codInstr), lower-case(\"public\")) or contains(lower-case($var/concept), lower-case(\"public\")) or contains(lower-case($var/../../docDscr/citation/titlStmt/titl), lower-case(\"public\")) or contains(lower-case($var/../../docDscr/citation/prodStmt/prodDate/@date), lower-case(\"public\")))  order by $var/@name descending  return $var } </dataDscr></codeBook>",
					new SearchQuery("variables",
							"allfields=*house*,allfields=*public*",
							"-variablename", "").getXquery());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
