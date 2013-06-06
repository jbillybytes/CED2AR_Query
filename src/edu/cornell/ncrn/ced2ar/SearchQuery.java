package edu.cornell.ncrn.ced2ar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author jerloc
 * 
 */
public class SearchQuery {

	private final static Logger logger = Logger.getLogger(SearchQuery.class
			.getName());

	private ArrayList<String> RETURN_FIELDS = new ArrayList<String>();
	private ArrayList<String> WHERE_FIELDS = new ArrayList<String>();
	private ArrayList<String> CODEBOOK_SORT_FIELDS = new ArrayList<String>();
	private ArrayList<String> DOCDSCR_SORT_FIELDS = new ArrayList<String>();
	private String RETURN_FIELD = null;
	private String WHERE = null;
	private String SORT = null;
	private String LIMIT = null;
	public String ERROR_MSG = "";
	private HashMap<String, String> API_DDI_PATH = new HashMap<String, String>();

	// constructors (a minimum of returnField is necessary to perform a query)
	public SearchQuery(String returnField) throws Exception {
		if (returnField != null && returnField.length() > 0) {
			initializeFieldsAndPaths();
			RETURN_FIELD = returnField.toLowerCase();
		} else {
			throw new Exception("Return field is required.");
		}
	}

	public SearchQuery(String returnField, String where, String sort,
			String limit) throws Exception {
		if (returnField != null && returnField.length() > 0) {
			initializeFieldsAndPaths();
			RETURN_FIELD = returnField.toLowerCase();
		} else {
			throw new Exception("Return field is required.");
		}

		WHERE = where;
		SORT = sort;
		LIMIT = limit;
	}

	// gets and sets
	public String getReturnField() {
		return RETURN_FIELD;
	}

	public void setReturnField(String returnField) {
		RETURN_FIELD = returnField;
	}

	public String getWhere() {
		return WHERE;
	}

	public void setWhere(String where) {
		WHERE = where;
	}

	public String getSort() {
		return SORT;
	}

	public void setSort(String sort) {
		SORT = sort;
	}

	public String getLimit() {
		return LIMIT;
	}

	public void setLimit(String limit) {
		LIMIT = limit;
	}

	public String getLimitRange() {
		String rangeStmt = "";
		if (LIMIT != null && LIMIT.length() > 0) {
			String[] ranges = LIMIT.split("-");

			if (ranges != null && ranges.length == 2) {
				rangeStmt = ", " + ranges[0].toString() + ", "
						+ ranges[1].toString() + ")";
			}
		}

		return rangeStmt;
	}

	public String getMainStatement() {
		String whereStmt = getWhereStatement();
		// for variables, we need to inject the where statement into the
		// codebook for loop, otherwise all the codebooks in the repo will be
		// returned whether they have a match or not
		// we also need to replace $var/ with blank
		String repWhere = (whereStmt == "" ? ""
				: " where $codebook/dataDscr/var["
						+ whereStmt.substring(whereStmt.indexOf("("),
								whereStmt.lastIndexOf(")") + 1).replace(
								"$var/", "") + "] ");
		String subsequence = (LIMIT != null && LIMIT.length() > 0) ? "subsequence("
				: "";

		String begStmt = "";
		switch (RETURN_FIELD.toLowerCase()) {
		case "variables":
			begStmt += "let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook "
					+ repWhere
					+ "return		<codeBook> { $codebook/docDscr }<dataDscr> {	"
					+ subsequence
					+ "for $var in $codebook/dataDscr/var"
					+ (whereStmt == "" ? "" : " where " + whereStmt);
			break;
		case "codebooks":
		case "documentdescriptions":
			begStmt += "let $ced2ar := collection('CED2AR') for $codebook in $ced2ar/codeBook let $var := $codebook/dataDscr/var "
					+ repWhere;
			break;
		}

		return begStmt;
	}

	public String getReturnStatement() {
		String subRange = (LIMIT != null && LIMIT.length() > 0) ? getLimitRange()
				: "";

		String returnStmt = " return ";

		switch (RETURN_FIELD.toLowerCase()) {
		case "variables":
			returnStmt += "$var " + subRange + "} </dataDscr></codeBook>";
			break;
		case "codebooks":
			returnStmt += "$var/../../docDscr/citation/titlStmt/titl";
			break;
		case "documentdescriptions":
			returnStmt += "$var/../../docDscr";
			break;
		}

		return returnStmt;
	}

	public String getSortStatement() {
		String sortStmt = "";
		String direction = "";

		if (SORT != null && SORT.length() > 0) {
			String[] sortTerms = SORT.split(",");

			for (int i = 0; i < sortTerms.length; i++) {
				String sortTerm = sortTerms[i];

				if (sortTerm.indexOf("-") >= 0) {
					direction = "descending";
				}

				String concat = i > 0 ? ", " : "";

				sortStmt += concat
						+ API_DDI_PATH.get(sortTerm.toLowerCase()
								.replace("+", "").replace(" ", "")
								.replace("-", "")) + " " + direction + " ";
			}

			return " order by " + sortStmt;
		} else
			return "";
	}

	public String getWhereStatement() {
		StringBuilder whereBuilder = new StringBuilder();

		// parse where
		if (WHERE != null && WHERE.length() > 0) {
			// split the string by , or |, indicating 'and' and 'or' expressions
			String[] termArray = WHERE.split("[,\\|]");

			// for (int i = 0; i < termArray.length; i++){
			// logger.info(termArray[i]);
			// }

			// loop through each term and split it by the = sign
			for (int i = 0; i < termArray.length; i++) {
				String expression = termArray[i];
				String bOperator = "";

				// BOOLEAN OPERATOR...
				// logger.info(expression);
				if (i > 0) {
					bOperator = WHERE.substring(WHERE.indexOf(expression) - 1,
							WHERE.indexOf(expression));
					// logger.info(bOperator);
				}

				// COMPARE OPERATOR...
				// replace == with = and replace <> with !=
				String refExp = expression.replace("==", "=").replace("<>",
						"!=");

				// every where term must consist of two terms separated by a (=,
				// !=, <, >)
				String[] fieldAndValue = refExp
						.split("=(?!=)|(\\!=)|<(?!>)|>(?!<)");

				// build where statement
				String field = fieldAndValue[0].toString();
				String value = fieldAndValue[1].toString();

				// logger.info(String.format("field: %s | value: %s", field,
				// value));

				// get the operator (=,!=,<,>)
				String operator = refExp.replace(field, "").replace(value, "");

				// logger.info(operator);

				if (field != null && field.length() > 0 && value != null
						&& value.length() > 0) {
					ArrayList<String> whereSubs = new ArrayList<String>();
					String whereSubStmt = "";

					if (field.toLowerCase().equals("allfields")) {
						for (String f : WHERE_FIELDS) {
							if (!f.equals("allfields")) {
								whereSubStmt = getWhereSubStatement(f, value,
										operator);
								whereSubs.add(whereSubStmt);
							}
						}

						// if the operator is an =, than make the subqueries
						// joined by or, otherwise join them by and
						// for != example, where (variablename != 'abc' and
						// variablename != '123')
						// for = example, where (variablename = 'abc' or
						// variablename = '123'
						// logger.info(operator);
						String joiner = (operator.equals("=") ? " or "
								: (operator.equals("!=") ? " and not "
										: " and "));
						String concat = i > 0 ? bOperator.replace(",", "and")
								.replace("|", "or") : "";
						whereBuilder.append(concat + " ("
								+ join(whereSubs, joiner) + ") ");
						logger.info(bOperator + " is the boolean operator!!!!");
					} else {
						whereSubStmt = getWhereSubStatement(field, value,
								operator);
						// if this is the first expression, don't add the
						// operator before it, otherwise, add the operator
						String joinNextExp = i == 0 ? "" : (bOperator.replace(
								",", (operator.trim().equals("!=") ? "and not"
										: "and")).replace("|", "or"));
						// logger.info(String.format("operator equals != ? %s",
						// logger.info(joinNextExp);
						whereBuilder.append(joinNextExp + " (" + whereSubStmt
								+ ") ");
					}
				}
			}
		}

		if (whereBuilder != null && whereBuilder.length() > 0) {
			return whereBuilder.toString();
		} else {
			return "";
		}
	}

	public String getWhereSubStatement(String field, String value,
			String operator) {

		// build case insensitive expression
		String expression = "";

		// if there is no wildcard character *, just build the simple expression
		if (value.indexOf("*") < 0) {
			// if the operator is a !=, strip out the ! b/c the negating of the
			// equality is handled in the joining of the expression (in the
			// getWhere method)
			expression = "lower-case(" + API_DDI_PATH.get(field.toLowerCase())
					+ ") " + operator.replace("!", "") + " lower-case(\""
					+ value + "\")";
		} else {
			// logger.info(String.format("wildcard index: %s",value.indexOf("*")));
			String effectiveValue = value.replace("*", "");
			if (value.indexOf("*") == 0
					&& value.lastIndexOf("*") == value.length() - 1) {
				// contains
				// logger.info(operator);
				expression = "contains(lower-case("
						+ API_DDI_PATH.get(field.toLowerCase())
						+ "), lower-case(\"" + effectiveValue + "\"))";
			} else if (value.indexOf("*") == 0) {
				// starts with
				expression = "ends-with(lower-case("
						+ API_DDI_PATH.get(field.toLowerCase())
						+ "), lower-case(\"" + effectiveValue + "\"))";
			} else if (value.lastIndexOf("*") == value.length() - 1) {
				// ends with
				expression = "starts-with(lower-case("
						+ API_DDI_PATH.get(field.toLowerCase())
						+ "), lower-case(\"" + effectiveValue + "\"))";
			}
		}

		logger.info(expression);
		return expression;
	}

	public String getXquery() {
		// XQUERY REFERENCE SAMPLES

		// 1. FOR VARIABLES, RETURN VARIABLES WHERE VARIABLE LABEL CONTAINS WAR
		// AND THE VARIABLE NAME = WARD
		// let $ced2ar := collection('CED2AR')
		// for $codebook in $ced2ar/codeBook
		// where $codebook/dataDscr/var[starts-with(lower-case(@name),
		// lower-case("a")) and contains(lower-case(txt), lower-case("app"))]
		// return
		// <codeBook> { $codebook/docDscr }<dataDscr> {
		// for $var in $codebook/dataDscr/var
		// where (starts-with(lower-case($var/@name), lower-case("a"))) and
		// (contains(lower-case($var/txt), lower-case("app"))) return $var
		// } </dataDscr></codeBook>

		// 2. FOR CODEBOOKS, RETURN CODEBOOKS WHERE variable label contains war
		// and variable name = ward
		// let $ced2ar := collection('CED2AR')
		// for $codebook in $ced2ar/codeBook
		// let $var := $codebook/dataDscr/var
		// where $codebook/dataDscr/var[(starts-with(lower-case(@name),
		// lower-case("a")))]
		// return $var/../..

		// 3. FOR DOCUMENT DESCRIPTION, RETURN DOCUMENT DESCRIPTION WHERE
		// VARIABLE NAME = cur_endmar_flag
		// let $ced2ar := collection('CED2AR')
		// for $codebook in $ced2ar/codeBook
		// let $var := $codebook/dataDscr/var
		// where $codebook/dataDscr/var[(starts-with(lower-case(@name),
		// lower-case("a")))]
		// return $var/../../docDscr

		String xquery = "";

		String returnStmt = getReturnStatement();
		String mainStmt = getMainStatement();
		String sortStmt = getSortStatement();

		xquery += mainStmt + sortStmt + returnStmt;

		return xquery;
	}

	private void initializeFieldsAndPaths() {
		// in constructing the where statement, everything is anchored to the
		// var element
		// so, if you want to include a field 'above' var in the xml tree, use
		// .. to traverse the appropriate number of levels
		// then proceed with the xpath. Ex.: var/../.. gets you to codebook, so
		// var/../../docDscr/citation/prodStmt/prodDate
		// would get you from var, up to codebook and then back down the tree to
		// prodDate

		// here, the DDI PATH value $var indicates the xpath:
		// codebook/datadscr/var
		// these are case sensitive b/c xquery is case sensitive
		API_DDI_PATH.put("variables", "$var");
		API_DDI_PATH.put("codebooks", "$var/../..");
		API_DDI_PATH.put("documentdescriptions", "$var/../../docDscr");
		API_DDI_PATH.put("variablename", "$var/@name");
		API_DDI_PATH.put("variablelabel", "$var/labl");
		API_DDI_PATH.put("variabletext", "$var/txt");
		API_DDI_PATH.put("variablecodeinstructions", "$var/codInstr");
		API_DDI_PATH.put("variableconcept", "$var/concept");
		API_DDI_PATH.put("codebooktitle",
				"$var/../../docDscr/citation/titlStmt/titl");
		API_DDI_PATH.put("productdate",
				"$var/../../docDscr/citation/prodStmt/prodDate/@date");

		// initialize acceptable return fields
		RETURN_FIELDS.add("variables");
		RETURN_FIELDS.add("codebooks");
		RETURN_FIELDS.add("documentdescriptions");

		// initialize acceptable where fields
		WHERE_FIELDS.add("variablename");
		WHERE_FIELDS.add("variablelabel");
		WHERE_FIELDS.add("variabletext");
		WHERE_FIELDS.add("variablecodeinstructions");
		WHERE_FIELDS.add("variableconcept");
		WHERE_FIELDS.add("codebooktitle");
		WHERE_FIELDS.add("productdate");
		WHERE_FIELDS.add("allfields");

		// the only legal sort field for codebooks is...
		CODEBOOK_SORT_FIELDS.add("codebooktitle");

		// the only valid sort fields for document descriptions are...
		DOCDSCR_SORT_FIELDS.add("codebooktitle");
		DOCDSCR_SORT_FIELDS.add("productdate");
	}

	public String join(ArrayList<String> list, String conjunction) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String item : list) {
			if (first)
				first = false;
			else
				sb.append(conjunction);
			sb.append(item);
		}
		return sb.toString();
	}

	public void validateRequest() {
		ERROR_MSG = "";
		validateReturnField();
		validateWhere();
		validateSort();
		validateLimit();
		logger.info(ERROR_MSG);
	}

	public void validateLimit() {
		// validate limit
		if (LIMIT != null && LIMIT.length() > 0) {
			// split limit by '-'
			String[] limitRange = LIMIT.split("-");

			if (limitRange.length != 2) {
				ERROR_MSG += "Too many ranges. Limit must be a range between two numbers. For example: 1-20.";
			}
			logger.info(limitRange[0] + ", " + limitRange[1]);
			// must only contain numbers
			if (!limitRange[0].matches("[0-9]+")
					|| !limitRange[1].matches("[0-9]+")) {
				ERROR_MSG += "Limit range must contain only numeric characters.";
			}
		}
	}

	public void validateReturnField() {
		// validate ReturnField
		// at least one returnElement is required - must be a valid return field
		if (RETURN_FIELD == null || RETURN_FIELD.length() == 0) {
			ERROR_MSG = "No return field was given. Valid return fields consist of: "
					+ join(RETURN_FIELDS, ", ");
		} else if (!RETURN_FIELDS.contains(RETURN_FIELD.toLowerCase())) {
			ERROR_MSG = "\""
					+ RETURN_FIELD
					+ "\" is an invalid return fields. Please choose one of the following fields: "
					+ join(RETURN_FIELDS, " or ");
		}
	}

	public void validateSort() {
		// validate sort
		if (SORT != null && SORT.length() > 0) {
			// split out sort by ,
			String[] sortTerms = SORT.split(",");

			for (int i = 0; i < sortTerms.length; i++) {
				String sortTerm = sortTerms[i].toLowerCase().replace("+", "")
						.replace(" ", "").replace("-", "");

				if (!WHERE_FIELDS.contains(sortTerm)
						|| sortTerm.equals("allfields")) {
					ERROR_MSG += "\""
							+ sortTerm
							+ "\" is an invalid sort term. Valid sort terms include: "
							+ join(WHERE_FIELDS, " or ").replace(
									"or allfields", "");
				}

				if (RETURN_FIELD.equals("codebooks")
						&& !CODEBOOK_SORT_FIELDS.contains(sortTerm)) {
					ERROR_MSG += "\""
							+ sortTerm
							+ "\" is an invalid sort term. When searching codebooks, the following sort terms are valid: "
							+ join(CODEBOOK_SORT_FIELDS, " or ");
				}

				if (RETURN_FIELD.equals("documentdescriptions")
						&& !DOCDSCR_SORT_FIELDS.contains(sortTerm)) {
					logger.info("invalid!!!!");
					ERROR_MSG += "\""
							+ sortTerm
							+ "\" is an invalid sort term.When searching document descriptions, the following sort terms are valid: "
							+ join(DOCDSCR_SORT_FIELDS, " or ");
				}
			}
		}
	}

	public void validateWhere() {
		// validate where
		if (WHERE != null && WHERE.length() > 0) {
			String[] termArray = WHERE.split("[,\\|]");

			// loop through each term and split it by the = sign
			for (int i = 0; i < termArray.length; i++) {

				String expression = termArray[i];

				if (expression.contains("><")) {
					ERROR_MSG += "'><' not a valid operator.";
					break;
				} else {
					// replace == with = and replace <> with !=
					String testExp = expression.replace("==", "=").replace(
							"<>", "!=");

					// every where term must consist of two terms separated by a
					// (=, !=, <, >)
					String[] fieldAndValue = testExp
							.split("=(?!=)|(\\!=)|<(?!>)|>(?!<)");

					if (fieldAndValue.length != 2) {
						ERROR_MSG += "Invalid number of operators found: "
								+ testExp;
						break;
					} else {
						String field = fieldAndValue[0];
						String value = fieldAndValue[1];
						if (field != null && field.length() > 0) {
							if (!WHERE_FIELDS.contains(field.toLowerCase())) {
								ERROR_MSG += " \""
										+ fieldAndValue[0]
										+ "\" is not a valid search field. The list of search fields is: "
										+ join(WHERE_FIELDS, ", ");
							}
						} else {
							ERROR_MSG += "Error: no field supplied.";
						}

						if (value != null && value.length() > 0) {
							// check to make sure wildcards are only at the
							// beginning and/or end
							if (value.indexOf("*") >= 0) {
								int firstWildCard = value.indexOf("*");
								int lastWildCard = value.lastIndexOf("*");
								if (firstWildCard < 0
										&& lastWildCard < value.length()) {
									ERROR_MSG += "Wildcard (*) is only allowed at the ends of a value. For example: *abc is valid, abc* is valid, *abc* is valid, ab*c is not valid.";
								}
							}
						} else {
							ERROR_MSG += "Error: no value supplied.";
						}
					}
				}
			}
		}
	}

}
