<html lang="en">
<body>
<#if reportTitle??>
    <h2>${reportTitle}</h2>
</#if>
<table style='border:1px solid;text-align:left;' width='100%'>
    <tr>
        <#list headers as item>
            <th style='border:1px solid;text-align:left;' >${item}</th>
        </#list>
    </tr>
    <#list reportTable as reportline>
        <tr>
        <#if reportline.link??>
            <td style='border:1px solid;text-align:left;'><a href="${reportline.link}">${reportline.name}</a></td>
        <#elseif reportline.name??>
            <td style='border:1px solid;text-align:left;'>${reportline.name}</td>
        </#if>
        <#if reportline.values??>
            <#list reportline.values as value>
                <td style='border:1px solid;text-align:left;' >${value}</td>
            </#list>
        </#if>
        </tr>
    </#list>
    <tr>
        <#if total??>
            <#list total as item>
                <td style='border:1px solid;text-align:left;' >${item}</td>
            </#list>
        </#if>
    </tr>
</table>
<#if description??>
    <#list description as item>
        <br>
        <label>${item}</label>
    </#list>
</#if>
</body>
</html>