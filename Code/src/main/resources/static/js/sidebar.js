function renderSidebar(active){
 const items=[
  {key:"overview",label:"Overview",icon:"🏠",href:"/student-home.html"},
  {key:"practice",label:"Practice",icon:"📚",href:"/subject.html?subject=JAVA"},
  {key:"tests",label:"Tests",icon:"📝",href:"/tests.html"},
  {key:"matrix",label:"Skill Matrix",icon:"🎯",href:"/skill-matrix.html"},
  {key:"achievements",label:"Achievements",icon:"🏆",href:"/achievements.html"},
  {key:"settings",label:"Settings",icon:"⚙️",href:"/settings.html"}
 ];
 const sidebar=document.getElementById("sidebar");if(!sidebar)return;
 sidebar.innerHTML=`<div class="brand"><span class="brand-badge">🧠</span> AdaptQ</div>
 ${items.map(i=>`<div class="nav-item ${i.key===active?"active":""}" onclick="location.href='${i.href}'"><span>${i.icon}</span>${i.label}</div>`).join("")}`;
}