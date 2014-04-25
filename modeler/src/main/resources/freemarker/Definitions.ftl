<#global tableTypeMapping = {"TABLE":"Table", "VIEW":"TableOrView", "SYNONYM":"Table"}/>

<#function isPrimaryKey column>
  <#if ! column.owner.primaryKey?? ><#return false></#if>
  <#list column.owner.primaryKey.columns as pk>
      <#if pk.properties.JAVA_NAME == column.properties.JAVA_NAME>
        <#return true>
      </#if>
  </#list>
  <#return false>
</#function>


