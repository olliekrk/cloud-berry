import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {BrowserModule} from "@angular/platform-browser";
import {RouterModule, Routes} from "@angular/router";
import {MainDashboardComponent} from "./component/main-dashboard/main-dashboard.component";
import {TopologyDashboardComponent} from "./component/topology-dashboard/topology-dashboard.component";
import {ConfigurationDashboardComponent} from "./component/configuration-dashboard/configuration-dashboard.component";


const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    redirectTo: "dashboard",
  },
  {
    path: "dashboard",
    component: MainDashboardComponent,
  },
  {
    path: "topology",
    component: TopologyDashboardComponent,
  },
  {
    path: "configuration",
    component: ConfigurationDashboardComponent,
  },
  {
    path: "**",
    pathMatch: "full",
    redirectTo: "dashboard"
  }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes, {
      useHash: true
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
