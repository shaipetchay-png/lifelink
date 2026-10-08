(function(){
 const toggle=document.getElementById('themeToggle');
 if(localStorage.getItem('lifelink-theme')==='dark') document.body.classList.add('dark-mode');
 if(toggle) toggle.addEventListener('click',()=>{document.body.classList.toggle('dark-mode');localStorage.setItem('lifelink-theme',document.body.classList.contains('dark-mode')?'dark':'light');});
 const menu=document.getElementById('mobileMenu'), sidebar=document.getElementById('sidebar');
 if(menu&&sidebar) menu.addEventListener('click',()=>sidebar.classList.toggle('open'));
 const n=document.getElementById('notificationButton'); if(n)n.addEventListener('click',()=>alert('You currently have no new notifications.'));
 const logout=document.getElementById('logoutButton'); if(logout)logout.addEventListener('click',()=>{if(confirm('Are you sure you want to logout?')) window.location.href='/logout';});
})();
