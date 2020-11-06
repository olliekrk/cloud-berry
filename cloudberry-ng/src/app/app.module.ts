import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";

import {AppComponent} from "./app.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppRoutingModule} from "./app-routing.module";
import {RouterModule} from "@angular/router";
import {MainDashboardComponent} from "./component/main-dashboard/main-dashboard.component";
import {LayoutModule} from "@angular/cdk/layout";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatListModule} from "@angular/material/list";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatCardModule} from "@angular/material/card";
import {MatMenuModule} from "@angular/material/menu";
import {ConfigurationDashboardComponent} from "./component/configuration-dashboard/configuration-dashboard.component";
import {NavigationToolbarComponent} from "./component/navigation-toolbar/navigation-toolbar.component";
import {LoggerConfig, LoggerModule} from "ngx-logger";
import {HttpClientModule} from "@angular/common/http";
import {TopologyDashboardComponent} from "./component/topology-dashboard/topology-dashboard.component";

@NgModule({
  declarations: [
    AppComponent,
    MainDashboardComponent,
    TopologyDashboardComponent,
    ConfigurationDashboardComponent,
    NavigationToolbarComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    RouterModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    LoggerModule,
    HttpClientModule,
  ],
  providers: [
    LoggerConfig,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
