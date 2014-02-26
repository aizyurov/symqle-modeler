<#global tableTypeMapping = {"TABLE":"Table", "VIEW":"TableOrView", "SYNONYM":"Table"}/>
<#global columnTypeMapping = { "-7":"Long", "-6":"Byte", "5":"Short", "4":"Integer", "-5":"Long",
  "6":"Float", "7":"Float", "8":"Double", "2":"Number", "3":"Number",
  "1":"String", "12":"String", "-1":"String",
  "91":"Date", "92":"Time", "93":"Timestamp",
  "-2":"byte[]", "-3":"byte[]", "-4":"byte[]",
  "2004":"byte[]", "2005":"String", "16":"Boolean"}/>
<#global mapper = {"Long":"LONG", "Byte":"BYTE", "Short":"SHORT", "Integer":"INTEGER", "String":"STRING",
  "Float":"FLOAT", "Double":"DOUBLE", "Number":"NUMBER",
  "Date":"DATE", "Time":"TIME", "Timestamp":"TIMESTAMP", "byte[]":"BYTES", "Boolean":"BOOLEAN"}/>
<#global requiredImport = {"Date":"java.sql.Date", "Time":"java.sql.Time", "Timestamp":"java.sql.Timestamp"}/>

