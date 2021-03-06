<@extends src="base.ftl">
<@block name="title">All Extension Points</@block>
<@block name="header_scripts">
  <script type="text/javascript" src="${skinPath}/script/jquery.tablesorter.js"></script>
  <script type="text/javascript" src="${skinPath}/script/jquery.tablesorter_filter.js"></script>
</@block>

<#if Root.isEmbeddedMode()>
  <#assign hideNav=true/>
</#if>


<@block name="right">
<#include "/docMacros.ftl">

<h1>${eps?size} Extension Points</h1>
<div class="tabscontent">
  <table id="contentTable" class="tablesorter">
  <thead>
    <tr>
      <th><@tableFilterArea "extension point"/></th>
    </tr>
  </thead>
  <tbody>
    <#list eps as ep>
    <tr>
      <td>
        <div>
          <h4><a title="Extension Point" href="${Root.path}/${distId}/viewExtensionPoint/${ep.id}" class="itemLink">${ep.label}</a></h4>
          <div class="itemDetail">
            <#if showDesc>
              <@inlineEdit ep.id descriptions[ep.id]/>
            </#if>
            <span title="Component Label">${ep.simpleId?replace(".*\\.","","r")}</span> -
            <span title="Component ID">${ep.simpleId}</span>
          </div>
        </div>
      </td>
    </tr>
    </#list>
  </tbody>
  </table>
</div>

</@block>

<@block name="footer_scripts">
  <@tableSortFilterScript "#contentTable" "[0,0]" />
</@block>

</@extends>
